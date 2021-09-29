package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSRdsSubnetGroup;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFExpression;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFList;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBSubnetGroup;
import software.amazon.awssdk.services.rds.model.DescribeDbSubnetGroupsResponse;
import software.amazon.awssdk.services.rds.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rds.model.Tag;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportRdsSubnetGroups extends AbstractExport<RdsClient> {

    @Override
    protected Maps<Resource> export(RdsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSRdsSubnetGroup> dbSubnetGroups = getDBSubnetGroups(client);

        return getResourceMaps(dbSubnetGroups);

    }

    List<AWSRdsSubnetGroup> getDBSubnetGroups(RdsClient client) {

        DescribeDbSubnetGroupsResponse describeDbSubnetGroupsResponse = client.describeDBSubnetGroups();
        return describeDbSubnetGroupsResponse.dbSubnetGroups().stream()
                .peek(dbSubnetGroup -> log.debug("dbSubnetGroup => {}", dbSubnetGroup))
                .map(dbSubnetGroup -> AWSRdsSubnetGroup.builder()
                        .dbSubnetGroup(dbSubnetGroup)
                        .tags(client.listTagsForResource(ListTagsForResourceRequest.builder()
                                .resourceName(dbSubnetGroup.dbSubnetGroupArn()).build()).tagList())
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSRdsSubnetGroup> awsRdsSubnetGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsRdsSubnetGroups.forEach(awsRdsSubnetGroup -> {
            DBSubnetGroup dbSubnetGroup = awsRdsSubnetGroup.getDbSubnetGroup();
            List<Tag> tags = awsRdsSubnetGroup.getTags();
            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_db_subnet_group")
                                    .name(dbSubnetGroup.dbSubnetGroupName())
                                    .argument("name", TFString.build(dbSubnetGroup.dbSubnetGroupName()))
                                    .argument("subnet_ids", TFList.builder().isLineIndent(false)
                                            .lists(dbSubnetGroup.subnets().stream()
                                                    .map(subnet -> TFExpression.builder().isLineIndent(false)
                                                            .expression(
                                                                    MessageFormat.format("aws_subnet.{0}.id", subnet.subnetIdentifier()))
                                                            .build())
                                                    .collect(Collectors.toList()))
                                            .build())
                                    .argument("tags", TFMap.build(
                                            tags.stream()
                                                    .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                    ))
                                    .build()
                    )
                    .build();

        });

        return resourceMapsBuilder.build();
    }

}
