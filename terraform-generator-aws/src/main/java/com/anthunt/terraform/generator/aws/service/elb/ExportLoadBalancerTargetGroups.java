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
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportLoadBalancerTargetGroups extends AbstractExport<ElasticLoadBalancingV2Client> {

    @Override
    protected Maps<Resource> export(ElasticLoadBalancingV2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSTargetGroup> awsLoadBalancers = getTagetGroups(client);
        return getResourceMaps(awsLoadBalancers);

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
                        .targetDescriptions(
                                client.describeTargetHealth(DescribeTargetHealthRequest.builder()
                                                .targetGroupArn(targetGroup.targetGroupArn())
                                                .build())
                                        .targetHealthDescriptions().stream()
                                        .map(t -> t.target())
                                        .collect(Collectors.toList()))
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

            awsTargetGroup.getTargetDescriptions().stream().forEach(targetDescription ->
                    resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_lb_target_group_attachment")
                                    .name(MessageFormat.format("{0}-{1}", targetGroup.targetGroupName(), targetDescription.id())
                                            .replaceAll("\\.", "-"))
                                    .arguments(
                                            TFArguments.builder()
                                                    .argument("target_group_arn ", TFExpression.build(
                                                            MessageFormat.format("aws_lb_target_group.{0}.arn",
                                                                    targetGroup.targetGroupName())))
                                                    .argumentIf(targetGroup.targetType() == TargetTypeEnum.INSTANCE,
                                                            "target_id", TFExpression.build(
                                                                    MessageFormat.format("aws_instance.{0}.id",
                                                                            targetDescription.id())))
                                                    .argumentIf(targetGroup.targetType() == TargetTypeEnum.IP,
                                                            "target_id", TFString.build(targetDescription.id()))
                                                    .argumentIf(targetGroup.targetType() == TargetTypeEnum.LAMBDA,
                                                            "target_id", TFExpression.build(
                                                                    MessageFormat.format("aws_lambda_function.{0}.arn",
                                                                            targetDescription.id())))
                                                    .argumentIf(targetGroup.targetType() != TargetTypeEnum.LAMBDA,
                                                            "port", TFNumber.build(targetDescription.port().toString()))
                                                    //Todo: not implemented
                                                    .argumentIf(targetGroup.targetType() == TargetTypeEnum.LAMBDA,
                                                            "depends_on", TFExpression.build(
                                                                    MessageFormat.format("aws_lambda_permission.{0}",
                                                                            "xxxx")))
                                                    .build())
                                    .build()
                    ).build()
            );
        }
        return resourceMapsBuilder.build();
    }

}