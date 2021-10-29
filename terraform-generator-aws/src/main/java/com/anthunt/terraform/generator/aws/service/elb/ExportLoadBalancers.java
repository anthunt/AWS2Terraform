package com.anthunt.terraform.generator.aws.service.elb;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.elb.model.AWSLoadBalancer;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
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

    private static final String DEFAULT_OUTPUT_FILE_NAME = "LoadBalancers";

    @Override
    protected Maps<Resource> export(ElasticLoadBalancingV2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSLoadBalancer> awsLoadBalancers = listAwsLoadBalancers(client);
        return getResourceMaps(awsLoadBalancers);
    }

    @Override
    protected TFImport scriptImport(ElasticLoadBalancingV2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSLoadBalancer> awsLoadBalancers = listAwsLoadBalancers(client);
        return getTFImport(awsLoadBalancers);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSLoadBalancer> listAwsLoadBalancers(ElasticLoadBalancingV2Client client) {

        DescribeLoadBalancersResponse describeLoadBalancersResponse = client.describeLoadBalancers();
        List<AWSLoadBalancer> awsLoadBalancers = describeLoadBalancersResponse.loadBalancers().stream()
                .map(loadBalancer -> AWSLoadBalancer.builder()
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
//        return awsLoadBalancers;
        return awsLoadBalancers.stream()
                .filter(awsLoadBalancer -> awsLoadBalancer.getTags().stream()
                        .noneMatch(tag ->
                                tag.key().startsWith("kubernetes.io/cluster/") &&
                                        tag.value().equals("owned"))
                )
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSLoadBalancer> awsLoadBalancers) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSLoadBalancer awsLoadBalancer : awsLoadBalancers) {
            LoadBalancer loadBalancer = awsLoadBalancer.getLoadBalancer();
            List<LoadBalancerAttribute> attributes = awsLoadBalancer.getLoadBalancerAttributes();
            List<Tag> tags = awsLoadBalancer.getTags();
            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api(awsLoadBalancer.getTerraformResourceName())
                                    .name(awsLoadBalancer.getResourceName())
                                    .argument("name", TFString.build(loadBalancer.loadBalancerName()))
                                    .argument("internal", TFBool.build(loadBalancer.scheme() == LoadBalancerSchemeEnum.INTERNAL))
                                    .argument("load_balancer_type", TFString.build(loadBalancer.typeAsString()))
                                    .argumentIf(() -> loadBalancer.availabilityZones().stream()
                                                    .flatMap(zone -> zone.loadBalancerAddresses().stream())
                                                    .peek(address -> log.debug("1, LBName => {}, address => {}", loadBalancer.loadBalancerName(), address))
                                                    .allMatch(address -> address.allocationId() == null && address.privateIPv4Address() == null),
                                            "subnets", TFList.build(
                                                    loadBalancer.availabilityZones().stream()
                                                            .map(zone -> TFExpression.builder().isLineIndent(false)
                                                                    .expression(MessageFormat.format("aws_subnet.{0}.id", zone.subnetId()))
                                                                    .build())
                                                            .collect(Collectors.toList())))
                                    .argumentsIf(() -> loadBalancer.availabilityZones().stream()
                                                    .peek(zone -> log.debug("zone => {}", zone))
                                                    .flatMap(zone -> zone.loadBalancerAddresses().stream())
                                                    .peek(address -> log.debug("2, LBName => {}, address => {}", loadBalancer.loadBalancerName(), address))
                                                    .anyMatch(address -> address.allocationId() != null),
                                            "subnet_mapping", loadBalancer.availabilityZones().stream()
                                                    .flatMap(zone -> zone.loadBalancerAddresses().stream()
                                                            .map(address -> TFBlock.builder()
                                                                    .arguments(TFArguments.builder()
                                                                            .argument("subnet_id", TFExpression.builder()
                                                                                    .expression(MessageFormat.format("aws_subnet.{0}.id", zone.subnetId()))
                                                                                    .build())
                                                                            .argument("allocation_id ", TFString.builder()
                                                                                    .value(address.allocationId())
                                                                                    .build())
                                                                            .build())
                                                                    .build()))
                                                    .collect(Collectors.toList()))
                                    .argumentsIf(() -> loadBalancer.availabilityZones().stream()
                                                    .peek(zone -> log.debug("zone => {}", zone))
                                                    .flatMap(zone -> zone.loadBalancerAddresses().stream())
                                                    .peek(address -> log.debug("3, LBName => {}, address => {}", loadBalancer.loadBalancerName(), address))
                                                    .anyMatch(address -> address.privateIPv4Address() != null),
                                            "subnet_mapping", loadBalancer.availabilityZones().stream()
                                                    .flatMap(zone -> zone.loadBalancerAddresses().stream()
                                                            .map(address -> TFBlock.builder()
                                                                    .arguments(TFArguments.builder()
                                                                            .argument("subnet_id", TFExpression.builder()
                                                                                    .expression(MessageFormat.format("aws_subnet.{0}.id", zone.subnetId()))
                                                                                    .build())
                                                                            .argument("private_ipv4_address ", TFString.builder()
                                                                                    .value(address.privateIPv4Address())
                                                                                    .build())
                                                                            .build())
                                                                    .build()))
                                                    .collect(Collectors.toList()))
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
                    .build();
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSLoadBalancer> awsLoadBalancers) {
        return TFImport.builder()
                .importLines(awsLoadBalancers.stream()
                        .map(awsLoadBalancer -> TFImportLine.builder()
                                .address(awsLoadBalancer.getTerraformAddress())
                                .id(awsLoadBalancer.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }

}
