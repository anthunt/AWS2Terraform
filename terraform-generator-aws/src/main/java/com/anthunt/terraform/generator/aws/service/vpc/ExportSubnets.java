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

        List<Subnet> subnets = getSubnets(client);
        return getResourceMaps(subnets);
    }

    List<Subnet> getSubnets(Ec2Client client) {
        DescribeSubnetsResponse describeSubnetsResponse = client.describeSubnets();
        return describeSubnetsResponse.subnets();
    }

    Maps<Resource> getResourceMaps(List<Subnet> subnets) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        int i = 0;
        for(Subnet subnet : subnets) {
            log.debug("subnet => {}", subnet);
            TFArguments.TFArgumentsBuilder tfArgumentsBuilder = TFArguments.builder();

            tfArgumentsBuilder.argument("availability_zone_id", TFString.build(subnet.availabilityZoneId()));
            tfArgumentsBuilder.argument("cidr_block", TFString.build(subnet.cidrBlock()));
            if(!subnet.ipv6CidrBlockAssociationSet().isEmpty()) {
                tfArgumentsBuilder.argument("ipv6_cidr_block", TFString.build(subnet.ipv6CidrBlockAssociationSet().get(0).ipv6CidrBlock()));
            }
            tfArgumentsBuilder.argument("map_public_ip_on_launch", TFBool.build(subnet.mapPublicIpOnLaunch()));
            if(subnet.outpostArn() != null) {
                tfArgumentsBuilder.argument("outpost_arn", TFString.build(subnet.outpostArn()));
            }
            tfArgumentsBuilder.argument("assign_ipv6_address_on_creation", TFBool.build(subnet.assignIpv6AddressOnCreation()));
            tfArgumentsBuilder.argument("vpc_id", TFString.build(subnet.vpcId()));
            tfArgumentsBuilder.argument("tags", TFMap.build(
                    subnet.tags().stream()
                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
            ));

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_subnet")
                            .name("subnet" + i)
                            .arguments(tfArgumentsBuilder.build())
                            .build()
            );

            i++;
        }

        return resourceMapsBuilder.build();
    }
}
