package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeNatGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.NatGateway;

import java.util.List;

public class ExportNatGateways extends AbstractExport<Ec2Client> {
    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        DescribeNatGatewaysResponse describeNatGatewaysResponse = client.describeNatGateways();
        List<NatGateway> natGateways = describeNatGatewaysResponse.natGateways ();

        int i = 0;
        for(NatGateway natGateway : natGateways) {

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_nat_gateway")
                            .name("nat_gateway" + i)
                            .arguments(
                                    TFArguments.builder()

                                            .build()
                            )
                            .build()
            );
            i++;

        }

        return null;
    }
}
