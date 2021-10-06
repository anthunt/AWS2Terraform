package com.anthunt.terraform.generator.aws.service.cloudwatchlogs;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.cloudwatchlogs.model.AWSLogGroup;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportCloudWatchLogGroups extends AbstractExport<CloudWatchLogsClient> {

    @Override
    protected Maps<Resource> export(CloudWatchLogsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSLogGroup> awsLogGroups = listRepositories(client);

        return getResourceMaps(awsLogGroups);

    }

    List<AWSLogGroup> listRepositories(CloudWatchLogsClient client) {
        DescribeLogGroupsResponse describeLogGroupsResponse = client.describeLogGroups();
        return describeLogGroupsResponse.logGroups().stream()
                .map(logGroup -> AWSLogGroup.builder()
                        .logGroup(logGroup)
                        .tags(client.listTagsLogGroup(ListTagsLogGroupRequest.builder()
                                        .logGroupName(logGroup.logGroupName())
                                .build()).tags())
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSLogGroup> awsLogGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSLogGroup awsLogGroup : awsLogGroups) {
            LogGroup logGroup = awsLogGroup.getLogGroup();

            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_cloudwatch_log_group")
                                    .name(logGroup.logGroupName().startsWith("/") ? logGroup.logGroupName().substring(1).replaceAll("/", "-")
                                            : logGroup.logGroupName().replaceAll("/", "-"))
                                    .argument("name", TFString.build(logGroup.logGroupName()))
                                    .argument("tags", TFMap.build(
                                            awsLogGroup.getTags().entrySet().stream()
                                                    .collect(Collectors.toMap(Map.Entry::getKey, tag -> TFString.build(tag.getValue())))
                                    ))
                                    .build())
                    .build();
        }
        return resourceMapsBuilder.build();
    }
}
