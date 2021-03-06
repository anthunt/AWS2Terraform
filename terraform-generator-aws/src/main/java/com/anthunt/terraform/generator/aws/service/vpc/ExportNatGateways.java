package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeNatGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.NatGateway;
import software.amazon.awssdk.services.ec2.model.NatGatewayAddress;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportNatGateways extends AbstractExport<Ec2Client> {
    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        DescribeNatGatewaysResponse describeNatGatewaysResponse = client.describeNatGateways();
        List<NatGateway> natGateways = describeNatGatewaysResponse.natGateways ();

        int i = 0;
        for(NatGateway natGateway : natGateways) {
            List<NatGatewayAddress> natGatewayAddresses = natGateway.natGatewayAddresses();
            for(NatGatewayAddress natGatewayAddress : natGatewayAddresses) {
                resourceMapsBuilder.map(
                        Resource.builder()
                                .api("aws_nat_gateway")
                                .name("nat_gateway" + i)
                                .arguments(
                                        TFArguments.builder()
                                                .argument("allocation_id", TFString.build(natGatewayAddress.allocationId()))
                                                .argument("subnet_id", TFString.build(natGateway.subnetId()))
                                                .argument("tags", TFMap.build(
                                                        natGateway.tags().stream()
                                                                .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                                ))
                                                .build()
                                )
                                .build()
                );
                i++;
            }
        }

        return resourceMapsBuilder.build();
    }
}
