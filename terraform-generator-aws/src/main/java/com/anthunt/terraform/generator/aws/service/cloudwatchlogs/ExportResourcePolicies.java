package com.anthunt.terraform.generator.aws.service.cloudwatchlogs;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeResourcePoliciesResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.ResourcePolicy;

import java.util.List;

@Slf4j
@Service
public class ExportResourcePolicies extends AbstractExport<CloudWatchLogsClient> {

    @Override
    protected Maps<Resource> export(CloudWatchLogsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<ResourcePolicy> resourcePolicies = listResourcePolicies(client);

        return getResourceMaps(resourcePolicies);

    }

    List<ResourcePolicy> listResourcePolicies(CloudWatchLogsClient client) {
        DescribeResourcePoliciesResponse describeResourcePoliciesResponse = client.describeResourcePolicies();
        return describeResourcePoliciesResponse.resourcePolicies();
    }

    Maps<Resource> getResourceMaps(List<ResourcePolicy> resourcePolicies) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (ResourcePolicy resourcePolicy : resourcePolicies) {

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
}
