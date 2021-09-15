package com.anthunt.terraform.generator.aws.service.elb;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.elb.model.AWSTargetGroup;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetGroupAttributesRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetGroupsResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroupAttribute;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportLoadBalancerTargetGroups extends AbstractExport<ElasticLoadBalancingV2Client> {

    @Override
    protected Maps<Resource> export(ElasticLoadBalancingV2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSTargetGroup> loadBalancerDtos = getTagetGroups(client);
        return getResourceMaps(loadBalancerDtos);

    }

    List<AWSTargetGroup> getTagetGroups(ElasticLoadBalancingV2Client client) {

        DescribeTargetGroupsResponse describeTargetGroupsResponse = client.describeTargetGroups();
        return describeTargetGroupsResponse.targetGroups()
                .stream()
                .peek(targetGroup -> log.debug("targetGroup => {}", targetGroup))
                .map(targetGroup -> AWSTargetGroup.builder()
                        .targetGroup(targetGroup)
                        .targetGroupAttributes(
                                client.describeTargetGroupAttributes(DescribeTargetGroupAttributesRequest.builder()
                                                .targetGroupArn(targetGroup.targetGroupArn())
                                                .build())
                                        .attributes())
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSTargetGroup> awsTargetGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        for (AWSTargetGroup awsTargetGroup : awsTargetGroups) {
            TargetGroup targetGroup = awsTargetGroup.getTargetGroup();
            List<TargetGroupAttribute> attributes = awsTargetGroup.getTargetGroupAttributes();

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_lb_target_group")
                            .name(targetGroup.targetGroupName())
                            .arguments(
                                    TFArguments.builder()
                                            .argument("name", TFString.build(targetGroup.targetGroupName()))
                                            .argument("port", TFNumber.build(targetGroup.port().toString()))
                                            .argument("protocol", TFString.build(targetGroup.protocolAsString()))
                                            .argument("vpc_id", TFExpression.build(
                                                    MessageFormat.format("aws_vpc.{0}.id", targetGroup.vpcId())))
                                            .argument("target_type", TFString.build(targetGroup.targetTypeAsString()))
                                            .argument("deregistration_delay", TFNumber.builder()
                                                    .value(attributes.stream()
                                                            .filter(a -> a.key().equals("deregistration_delay.timeout_seconds"))
                                                            .map(a -> a.value())
                                                            .findFirst().orElse(null))
                                                    .build())
                                            .argument("health_check", TFBlock.builder()
                                                    .argument("enabled", TFBool.build(targetGroup.healthCheckEnabled()))
                                                    .argument("port", TFNumber.build(targetGroup.healthCheckPort()))
                                                    .argument("protocol", TFString.build(targetGroup.protocolAsString()))
                                                    .argument("path", TFString.build(targetGroup.healthCheckPath()))
                                                    .argument("healthy_threshold", TFNumber.build(targetGroup.healthyThresholdCount().toString()))
                                                    .argument("unhealthy_threshold", TFNumber.build(targetGroup.unhealthyThresholdCount().toString()))
                                                    .argument("interval", TFNumber.build(targetGroup.healthCheckIntervalSeconds().toString()))
                                                    .build()
                                            ).build()
                            ).build()
            ).build();
        }
        return resourceMapsBuilder.build();
    }

}
