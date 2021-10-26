package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSNatGateway;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
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
        List<AWSNatGateway> awsNatGateways = listAwsNatGateways(client);
        return getResourceMaps(awsNatGateways);
    }

    @Override
    protected TFImport scriptImport(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSNatGateway> awsNatGateways = listAwsNatGateways(client);
        return getTFImport(awsNatGateways);
    }

    protected List<AWSNatGateway> listAwsNatGateways(Ec2Client client) {
        DescribeNatGatewaysResponse describeNatGatewaysResponse = client.describeNatGateways();
        return describeNatGatewaysResponse.natGateways().stream()
                .map(natGateway -> AWSNatGateway.builder()
                        .natGateway(natGateway)
                        .build())
                .collect(Collectors.toList());
    }

    protected Maps<Resource> getResourceMaps(List<AWSNatGateway> awsNatGateways) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSNatGateway awsNatGateway : awsNatGateways) {
            NatGateway natGateway = awsNatGateway.getNatGateway();
            List<NatGatewayAddress> natGatewayAddresses = natGateway.natGatewayAddresses();
            for (NatGatewayAddress natGatewayAddress : natGatewayAddresses) {
                resourceMapsBuilder.map(
                        Resource.builder()
                                .api(awsNatGateway.getTerraformResourceName())
                                .name(awsNatGateway.getResourceName())
                                .argument("allocation_id", TFString.build(natGatewayAddress.allocationId()))
                                .argument("subnet_id", TFString.build(natGateway.subnetId()))
                                .argument("tags", TFMap.build(
                                        natGateway.tags().stream()
                                                .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                ))
                                .build()
                );
            }
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSNatGateway> awsNatGateways) {
        return TFImport.builder()
                .importLines(awsNatGateways.stream()
                        .map(awsNatGateway -> TFImportLine.builder()
                                .address(awsNatGateway.getTerraformAddress())
                                .id(awsNatGateway.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
