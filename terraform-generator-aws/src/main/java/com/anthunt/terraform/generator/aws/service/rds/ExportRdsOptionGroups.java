package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSRdsOptionGroup;
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
import software.amazon.awssdk.services.rds.model.DescribeOptionGroupsResponse;
import software.amazon.awssdk.services.rds.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rds.model.OptionGroup;
import software.amazon.awssdk.services.rds.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportRdsOptionGroups extends AbstractExport<RdsClient> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "RdsOptionGroups";

    @Override
    protected Maps<Resource> export(RdsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRdsOptionGroup> awsRdsOptionGroups = listAwsRdsOptionGroups(client);
        return getResourceMaps(awsRdsOptionGroups);
    }

    @Override
    protected TFImport scriptImport(RdsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRdsOptionGroup> awsRdsOptionGroups = listAwsRdsOptionGroups(client);
        return getTFImport(awsRdsOptionGroups);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSRdsOptionGroup> listAwsRdsOptionGroups(RdsClient client) {

        DescribeOptionGroupsResponse describeOptionGroupsResponse = client.describeOptionGroups();
        return describeOptionGroupsResponse.optionGroupsList().stream()
                .filter(optionGroup -> !optionGroup.optionGroupName().startsWith("default:"))
                .peek(optionGroup -> log.debug("optionGroup => {}", optionGroup))
                .map(optionGroup -> AWSRdsOptionGroup.builder()
                        .optionGroup(optionGroup)
                        .tags(client.listTagsForResource(ListTagsForResourceRequest.builder()
                                        .resourceName(optionGroup.optionGroupArn())
                                        .build())
                                .tagList())
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSRdsOptionGroup> awsRdsOptionGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsRdsOptionGroups.forEach(awsRdsOptionGroup -> {
            OptionGroup optionGroup = awsRdsOptionGroup.getOptionGroup();
            List<Tag> tags = awsRdsOptionGroup.getTags();

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api(awsRdsOptionGroup.getTerraformResourceName())
                            .name(awsRdsOptionGroup.getResourceName())
                            .argument("name", TFString.build(optionGroup.optionGroupName()))
                            .argument("engine_name", TFString.build(optionGroup.engineName()))
                            .argument("major_engine_version", TFString.build(optionGroup.majorEngineVersion()))
                            .argument("option_group_description", TFString.build(optionGroup.optionGroupDescription()))
                            .argumentsIf(Optional.ofNullable(optionGroup.options()).isPresent(),
                                    "option",
                                    () -> optionGroup.options().stream()
                                            .map(option -> TFBlock.builder()
                                                    .argument("option_name", TFString.build(option.optionName()))
                                                    .argumentsIf(Optional.ofNullable(option.optionSettings()).isPresent(),
                                                            "option_settings ",
                                                            () -> option.optionSettings().stream()
                                                                    .map(optionSetting -> TFBlock.builder()
                                                                            .argument("name", TFString.build(optionSetting.name()))
                                                                            .argument("value", TFString.build(optionSetting.value()))
                                                                            .build()
                                                                    ).collect(Collectors.toList()))
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

    TFImport getTFImport(List<AWSRdsOptionGroup> awsRdsOptionGroups) {
        return TFImport.builder()
                .importLines(awsRdsOptionGroups.stream()
                        .map(awsRdsOptionGroup -> TFImportLine.builder()
                                .address(awsRdsOptionGroup.getTerraformAddress())
                                .id(awsRdsOptionGroup.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }

}
