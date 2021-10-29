package com.anthunt.terraform.generator.aws.service.cloudwatchlogs;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.cloudwatchlogs.model.AWSResourcePolicy;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeResourcePoliciesResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.ResourcePolicy;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportResourcePolicies extends AbstractExport<CloudWatchLogsClient> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "CloudWatchResourcePolicies";

    @Override
    protected Maps<Resource> export(CloudWatchLogsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSResourcePolicy> awsResourcePolicies = listResourcePolicies(client);
        return getResourceMaps(awsResourcePolicies);
    }

    @Override
    protected TFImport scriptImport(CloudWatchLogsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSResourcePolicy> awsResourcePolicies = listResourcePolicies(client);
        return getTFImport(awsResourcePolicies);
    }

    @Override
    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSResourcePolicy> listResourcePolicies(CloudWatchLogsClient client) {
        DescribeResourcePoliciesResponse describeResourcePoliciesResponse = client.describeResourcePolicies();
        return describeResourcePoliciesResponse.resourcePolicies().stream()
                .map(resourcePolicy -> AWSResourcePolicy.builder()
                        .resourcePolicy(resourcePolicy)
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSResourcePolicy> awsResourcePolicies) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSResourcePolicy awsResourcePolicy : awsResourcePolicies) {
            ResourcePolicy resourcePolicy = awsResourcePolicy.getResourcePolicy();
            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_cloudwatch_log_resource_policy")
                                    .name(resourcePolicy.policyName())
                                    .argument("policy_name", TFString.build(resourcePolicy.policyName()))
                                    .argument("policy_document", TFString.builder().isMultiline(true)
                                            .value(resourcePolicy.policyDocument()).build())
                                    .build())
                    .build();
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSResourcePolicy> awsResourcePolicies) {
        return TFImport.builder()
                .importLines(awsResourcePolicies.stream()
                        .map(awsResourcePolicy -> TFImportLine.builder()
                                .address(awsResourcePolicy.getTerraformAddress())
                                .id(awsResourcePolicy.getResourceId())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
