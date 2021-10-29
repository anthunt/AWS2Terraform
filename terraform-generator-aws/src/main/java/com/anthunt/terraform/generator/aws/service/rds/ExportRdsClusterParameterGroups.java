package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSRdsClusterParameterGroup;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFBlock;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportRdsClusterParameterGroups extends AbstractExport<RdsClient> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "RdsClusterParameterGroups";

    @Override
    protected Maps<Resource> export(RdsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRdsClusterParameterGroup> awsRdsClusterParameterGroups = listAwsRdsClusterParameterGroups(client);
        return getResourceMaps(awsRdsClusterParameterGroups);
    }

    @Override
    protected TFImport scriptImport(RdsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRdsClusterParameterGroup> awsRdsClusterParameterGroups = listAwsRdsClusterParameterGroups(client);
        return getTFImport(awsRdsClusterParameterGroups);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSRdsClusterParameterGroup> listAwsRdsClusterParameterGroups(RdsClient client) {

        DescribeDbClusterParameterGroupsResponse describeDbClustersResponse = client.describeDBClusterParameterGroups();
        return describeDbClustersResponse.dbClusterParameterGroups().stream()
                .filter(parameterGroup ->
                        !parameterGroup.dbClusterParameterGroupName().startsWith("default."))
                .peek(parameterGroup -> log.debug("parameterGroup => {}", parameterGroup))
                .map(parameterGroup -> AWSRdsClusterParameterGroup.builder()
                        .dbClusterParameterGroup(parameterGroup)
                        .parameters(client.describeDBClusterParameters(DescribeDbClusterParametersRequest.builder()
                                        .dbClusterParameterGroupName(parameterGroup.dbClusterParameterGroupName())
                                        .build()).parameters().stream()
                                .peek(parameter -> log.debug("parameter => {}", parameter))
                                .collect(Collectors.toList()))
                        .tags(client.listTagsForResource(ListTagsForResourceRequest.builder()
                                .resourceName(parameterGroup.dbClusterParameterGroupArn()).build()).tagList())
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSRdsClusterParameterGroup> awsDbClusterParameterGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsDbClusterParameterGroups.forEach(awsDbClusterParameterGroup -> {
            DBClusterParameterGroup parameterGroup = awsDbClusterParameterGroup.getDbClusterParameterGroup();
            List<Parameter> parameters = awsDbClusterParameterGroup.getParameters();
            List<Tag> tags = awsDbClusterParameterGroup.getTags();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api(awsDbClusterParameterGroup.getTerraformResourceName())
                            .name(awsDbClusterParameterGroup.getResourceName())
                            .argument("name", TFString.build(parameterGroup.dbClusterParameterGroupName()))
                            .argument("family", TFString.build(parameterGroup.dbParameterGroupFamily()))
                            .argument("description", TFString.build(parameterGroup.description()))
                            .argumentsIf(Optional.ofNullable(parameters).isPresent(),
                                    "parameter",
                                    () -> parameters.stream()
                                            .filter(p -> p.source().equalsIgnoreCase("modified"))
                                            .map(parameter -> TFBlock.builder()
                                                    .argument("name", TFString.build(parameter.parameterName()))
                                                    .argument("value", TFString.build(parameter.parameterValue()))
                                                    .build())
                                            .collect(Collectors.toList()))
                            .argument("tags", TFMap.build(
                                    tags.stream()
                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                            ))
                            .build()
            );
        });
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSRdsClusterParameterGroup> awsRdsClusterParameterGroups) {
        return TFImport.builder()
                .importLines(awsRdsClusterParameterGroups.stream()
                        .map(awsRdsClusterParameterGroup -> TFImportLine.builder()
                                .address(awsRdsClusterParameterGroup.getTerraformAddress())
                                .id(awsRdsClusterParameterGroup.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
