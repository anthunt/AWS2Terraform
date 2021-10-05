package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFBool;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsResponse;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportSubnets extends AbstractExport<Ec2Client> {
    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<Subnet> subnets = listSubnets(client);
        return getResourceMaps(subnets);
    }

    List<Subnet> listSubnets(Ec2Client client) {
        DescribeSubnetsResponse describeSubnetsResponse = client.describeSubnets();
        return describeSubnetsResponse.subnets();
    }

    Maps<Resource> getResourceMaps(List<Subnet> subnets) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        int i = 0;
        for(Subnet subnet : subnets) {
            log.debug("subnet => {}", subnet);

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_subnet")
                            .name(subnet.subnetId())
                            .arguments(
                                    TFArguments.builder()
                                            .argument("availability_zone_id", TFString.build(subnet.availabilityZoneId()))
                                            .argument("cidr_block", TFString.build(subnet.cidrBlock()))
                                            .argumentIf(!subnet.ipv6CidrBlockAssociationSet().isEmpty(),
                                                    "ipv6_cidr_block",
                                                    () -> TFString.build(subnet.ipv6CidrBlockAssociationSet().get(0).ipv6CidrBlock())
                                            )
                                            .argument("map_public_ip_on_launch", TFBool.build(subnet.mapPublicIpOnLaunch()))
                                            .argumentIf(subnet.outpostArn() != null, "outpost_arn", TFString.build(subnet.outpostArn()))
                                            .argument("assign_ipv6_address_on_creation", TFBool.build(subnet.assignIpv6AddressOnCreation()))
                                            .argument("vpc_id", TFString.build(subnet.vpcId()))
                                            .argument("tags", TFMap.build(
                                                    subnet.tags().stream()
                                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                                    )
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        return resourceMapsBuilder.build();
    }
}
