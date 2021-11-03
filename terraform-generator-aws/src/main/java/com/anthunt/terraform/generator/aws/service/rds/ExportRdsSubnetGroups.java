package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSRdsSubnetGroup;
import com.anthunt.terraform.generator.aws.utils.ThreadUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFExpression;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFList;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
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

    private static final String DEFAULT_OUTPUT_FILE_NAME = "RdsSubnetGroups";

    @Override
    protected Maps<Resource> export(RdsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRdsSubnetGroup> awsRdsSubnetGroups = listAwsRdsSubnetGroups(client);
        return getResourceMaps(awsRdsSubnetGroups);
    }

    @Override
    protected TFImport scriptImport(RdsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRdsSubnetGroup> awsRdsSubnetGroups = listAwsRdsSubnetGroups(client);
        return getTFImport(awsRdsSubnetGroups);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSRdsSubnetGroup> listAwsRdsSubnetGroups(RdsClient client) {

        DescribeDbSubnetGroupsResponse describeDbSubnetGroupsResponse = client.describeDBSubnetGroups();
        return describeDbSubnetGroupsResponse.dbSubnetGroups().stream()
                .peek(dbSubnetGroup -> log.debug("dbSubnetGroup => {}", dbSubnetGroup))
                .map(dbSubnetGroup -> {
                    ThreadUtils.sleep(super.getDelayBetweenApis());
                    return AWSRdsSubnetGroup.builder()
                            .dbSubnetGroup(dbSubnetGroup)
                            .tags(client.listTagsForResource(ListTagsForResourceRequest.builder()
                                    .resourceName(dbSubnetGroup.dbSubnetGroupArn()).build()).tagList())
                            .build();
                })
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSRdsSubnetGroup> awsRdsSubnetGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsRdsSubnetGroups.forEach(awsRdsSubnetGroup -> {
            DBSubnetGroup dbSubnetGroup = awsRdsSubnetGroup.getDbSubnetGroup();
            List<Tag> tags = awsRdsSubnetGroup.getTags();
            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api(awsRdsSubnetGroup.getTerraformResourceName())
                                    .name(awsRdsSubnetGroup.getResourceName())
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
                    );
        });
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSRdsSubnetGroup> awsRdsSubnetGroups) {
        return TFImport.builder()
                .importLines(awsRdsSubnetGroups.stream()
                        .map(awsRdsSubnetGroup -> TFImportLine.builder()
                                .address(awsRdsSubnetGroup.getTerraformAddress())
                                .id(awsRdsSubnetGroup.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
