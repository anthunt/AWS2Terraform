package com.anthunt.terraform.generator.aws.service.elb;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.elb.dto.LoadBalancerDto;
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
public class ExportLoadBalancers extends AbstractExport<ElasticLoadBalancingV2Client> {

    @Override
    protected Maps<Resource> export(ElasticLoadBalancingV2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<LoadBalancerDto> loadBalancerDtos = getLoadBalancers(client);

        return getResourceMaps(loadBalancerDtos);

    }

    List<LoadBalancerDto> getLoadBalancers(ElasticLoadBalancingV2Client client) {

        DescribeLoadBalancersResponse describeLoadBalancersResponse = client.describeLoadBalancers();
        return describeLoadBalancersResponse.loadBalancers().stream()
                .map(loadBalancer -> LoadBalancerDto.builder()
                        .loadBalancer(loadBalancer)
                        .loadBalancerAttributes(
                                client.describeLoadBalancerAttributes(DescribeLoadBalancerAttributesRequest.builder()
                                                .loadBalancerArn(loadBalancer.loadBalancerArn())
                                                .build())
                                        .attributes())
                        .tags(
                                client.describeTags(DescribeTagsRequest.builder()
                                                .resourceArns(loadBalancer.loadBalancerArn())
                                                .build())
                                        .tagDescriptions().stream()
                                        .flatMap(o -> o.tags().stream())
                                        .collect(Collectors.toList()))
                        .build())
                .peek(o -> log.debug("LoadBalancerAttributes => {}", o.getLoadBalancerAttributes()))
                .peek(o -> log.debug("TagDescriptions => {}", o.getTags()))
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<LoadBalancerDto> loadBalancerDtos) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (LoadBalancerDto loadBalancerDto : loadBalancerDtos) {
            LoadBalancer loadBalancer = loadBalancerDto.getLoadBalancer();
            List<LoadBalancerAttribute> attributes = loadBalancerDto.getLoadBalancerAttributes();
            List<Tag> tags = loadBalancerDto.getTags();
            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_lb")
                                    .name(loadBalancer.loadBalancerName())
                                    .arguments(
                                            TFArguments.builder()
                                                    .argument("name", TFString.build(loadBalancer.loadBalancerName()))
                                                    .argument("internal", TFBool.build(loadBalancer.scheme() == LoadBalancerSchemeEnum.INTERNAL))
                                                    .argument("load_balancer_type", TFString.build(loadBalancer.typeAsString()))
                                                    .argument("subnets", TFList.build(
                                                            loadBalancer.availabilityZones().stream()
                                                                    .map(zone -> TFExpression.builder().isLineIndent(false)
                                                                            .expression(MessageFormat.format("aws_subnet.{0}.id", zone.subnetId()))
                                                                            .build())
                                                                    .collect(Collectors.toList())))
                                                    .argument("ip_address_type", TFString.builder()
                                                            .value(loadBalancer.ipAddressTypeAsString()).build())
                                                    .argument("enable_deletion_protection", TFBool.builder()
                                                            .bool(attributes.stream()
                                                                    .filter(a -> a.key().equals("deletion_protection.enabled"))
                                                                    .anyMatch(a -> a.value().equals("true")))
                                                            .build())
                                                    .argument("enable_cross_zone_load_balancing", TFBool.builder()
                                                            .bool(attributes.stream()
                                                                    .filter(a -> a.key().equals("load_balancing.cross_zone.enabled"))
                                                                    .anyMatch(a -> a.value().equals("true")))
                                                            .build())
                                                    .argument("tags", TFMap.build(
                                                            tags.stream()
                                                                    .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                                    ))
                                                    .build())
                                    .build())
                    .build();

        }
        return resourceMapsBuilder.build();

    }

}
