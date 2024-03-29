package com.anthunt.terraform.generator.aws.service.cloudwatchlogs;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.cloudwatchlogs.model.AWSLogGroup;
import com.anthunt.terraform.generator.aws.utils.ThreadUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFNumber;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.ListTagsLogGroupRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportCloudWatchLogGroups extends AbstractExport<CloudWatchLogsClient> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "CloudWatchLogGroups";

    @Override
    protected Maps<Resource> export(CloudWatchLogsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSLogGroup> awsLogGroups = listAwsLogGroups(client);
        return getResourceMaps(awsLogGroups);
    }

    @Override
    protected TFImport scriptImport(CloudWatchLogsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSLogGroup> awsLogGroups = listAwsLogGroups(client);
        return getTFImport(awsLogGroups);
    }

    @Override
    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSLogGroup> listAwsLogGroups(CloudWatchLogsClient client) {
        DescribeLogGroupsResponse describeLogGroupsResponse = client.describeLogGroups();
        return describeLogGroupsResponse.logGroups().stream()
                .map(logGroup -> {
                    ThreadUtils.sleep(super.getDelayBetweenApis());
                    return AWSLogGroup.builder()
                            .logGroup(logGroup)
                            .tags(client.listTagsLogGroup(ListTagsLogGroupRequest.builder()
                                    .logGroupName(logGroup.logGroupName())
                                    .build()).tags())
                            .build();
                })
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSLogGroup> awsLogGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSLogGroup awsLogGroup : awsLogGroups) {
            LogGroup logGroup = awsLogGroup.getLogGroup();

            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api(awsLogGroup.getTerraformResourceName())
                                    .name(awsLogGroup.getResourceName())
                                    .argument("name", TFString.build(logGroup.logGroupName()))
                                    .argument("retention_in_days", Optional.ofNullable(logGroup.retentionInDays())
                                            .map(TFNumber::build)
                                            .orElse(TFNumber.build(null)))
                                    .argument("kms_key_id", TFString.build(logGroup.kmsKeyId()))
                                    .argument("tags", TFMap.build(
                                            awsLogGroup.getTags().entrySet().stream()
                                                    .collect(Collectors.toMap(Map.Entry::getKey, tag -> TFString.build(tag.getValue())))
                                    ))
                                    .build())
                    .build();
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSLogGroup> awsLogGroups) {
        return TFImport.builder()
                .importLines(awsLogGroups.stream()
                        .map(awsLogGroup -> TFImportLine.builder()
                                .address(awsLogGroup.getTerraformAddress())
                                .id(awsLogGroup.getResourceId())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
