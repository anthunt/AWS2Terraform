package com.anthunt.terraform.generator.aws.service.elb;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.elb.model.AWSListener;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportLoadBalancerListeners extends AbstractExport<ElasticLoadBalancingV2Client> {

    @Override
    protected Maps<Resource> export(ElasticLoadBalancingV2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSListener> awsListeners = listAwsListeners(client);
        return getResourceMaps(awsListeners);

    }

    List<AWSListener> listAwsListeners(ElasticLoadBalancingV2Client client) {

        DescribeLoadBalancersResponse describeLoadBalancersResponse = client.describeLoadBalancers();
        return describeLoadBalancersResponse.loadBalancers().stream()
                .flatMap(loadBalancer -> client.describeListeners(DescribeListenersRequest.builder()
                                .loadBalancerArn(loadBalancer.loadBalancerArn()).build())
                        .listeners().stream()
                        .map(listener -> AWSListener.builder()
                                .listener(listener)
                                .loadBalancer(loadBalancer)
                                .targetGroup(listener.defaultActions().stream()
                                        .map(a -> a.targetGroupArn())
                                        .findFirst()
                                        .map(targetGroupArn -> client.describeTargetGroups(
                                                        DescribeTargetGroupsRequest.builder()
                                                                .targetGroupArns(targetGroupArn)
                                                                .build())
                                                .targetGroups().stream()
                                                .findFirst()
                                                .orElse(null))
                                        .orElse(null))
                                .build()))
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSListener> awslisteners) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        for (AWSListener awsListener : awslisteners) {
            Listener listener = awsListener.getListener();
            LoadBalancer loadBalancer = awsListener.getLoadBalancer();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_lb_listener")
                            .name(MessageFormat.format("{0}-{1}-{2}", loadBalancer.loadBalancerName(), listener.port(), listener.protocolAsString()))
                            .arguments(
                                    TFArguments.builder()
                                            .argument("load_balancer_arn", TFExpression.build(
                                                    MessageFormat.format("aws_lb.{0}.arn", loadBalancer.loadBalancerName())))
                                            .argument("port", TFNumber.build(listener.port()))
                                            .argument("protocol", TFString.build(listener.protocolAsString()))
                                            .argumentIf(listener.hasDefaultActions(), "default_action",
                                                    listener.defaultActions().stream()
                                                            .map(action -> TFBlock.builder()
                                                                    .argument("target_group_arn",
                                                                            TFExpression.build(Optional.ofNullable(awsListener.getTargetGroup())
                                                                                    .map(t -> MessageFormat.format("aws_lb_target_group.{0}.arn", t.targetGroupName()))
                                                                                    .orElse(null))
                                                                    )
                                                                    .argument("type", TFString.build(action.typeAsString()))
                                                                    .build())
                                                            .collect(Collectors.toList())
                                            )
                                            .build()
                            ).build()
            ).build();
        }
        return resourceMapsBuilder.build();
    }

}
