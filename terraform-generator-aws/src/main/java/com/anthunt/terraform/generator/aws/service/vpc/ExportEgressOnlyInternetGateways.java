package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSEgressOnlyInternetGateway;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeEgressOnlyInternetGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;
import software.amazon.awssdk.services.ec2.model.InternetGatewayAttachment;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportEgressOnlyInternetGateways extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSEgressOnlyInternetGateway> awsEgressOnlyInternetGateways = listAwsEgressOnlyInternetGateways(client);
        return getResourceMaps(awsEgressOnlyInternetGateways);
    }

    @Override
    protected TFImport scriptImport(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSEgressOnlyInternetGateway> awsEgressOnlyInternetGateways = listAwsEgressOnlyInternetGateways(client);
        return getTFImport(awsEgressOnlyInternetGateways);
    }

    protected List<AWSEgressOnlyInternetGateway> listAwsEgressOnlyInternetGateways(Ec2Client client) {
        DescribeEgressOnlyInternetGatewaysResponse describeEgressOnlyInternetGatewaysResponse = client.describeEgressOnlyInternetGateways();
        return describeEgressOnlyInternetGatewaysResponse.egressOnlyInternetGateways().stream()
                .map(egressOnlyInternetGateway -> AWSEgressOnlyInternetGateway.builder()
                        .egressOnlyInternetGateway(egressOnlyInternetGateway)
                        .build())
                .collect(Collectors.toList());
    }

    protected Maps<Resource> getResourceMaps(List<AWSEgressOnlyInternetGateway> awsEgressOnlyInternetGateways) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSEgressOnlyInternetGateway awsEgressOnlyInternetGateway : awsEgressOnlyInternetGateways) {
            EgressOnlyInternetGateway egressOnlyInternetGateway = awsEgressOnlyInternetGateway.getEgressOnlyInternetGateway();
            List<InternetGatewayAttachment> internetGatewayAttachments = egressOnlyInternetGateway.attachments();

            for (InternetGatewayAttachment internetGatewayAttachment : internetGatewayAttachments) {
                resourceMapsBuilder.map(
                        Resource.builder()
                                .api(awsEgressOnlyInternetGateway.getTerraformResourceName())
                                .name(egressOnlyInternetGateway.egressOnlyInternetGatewayId())
                                .argument("vpc_id", TFString.build(internetGatewayAttachment.vpcId()))
                                .argument("tags", TFMap.build(
                                        egressOnlyInternetGateway.tags().stream()
                                                .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                ))
                                .build()
                );
            }
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSEgressOnlyInternetGateway> awsEgressOnlyInternetGateways) {

        return TFImport.builder()
                .importLines(awsEgressOnlyInternetGateways.stream()
                        .map(awsEgressOnlyInternetGateway -> TFImportLine.builder()
                                .address(awsEgressOnlyInternetGateway.getTerraformAddress())
                                .id(awsEgressOnlyInternetGateway.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
