package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
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

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportEgressOnlyInternetGateways extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<EgressOnlyInternetGateway> egressOnlyInternetGateways = listEgressOnlyInternetGateways(client);
        return getResourceMaps(egressOnlyInternetGateways);
    }

    @Override
    protected TFImport scriptImport(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<EgressOnlyInternetGateway> egressOnlyInternetGateways = listEgressOnlyInternetGateways(client);
        return getTFImport(egressOnlyInternetGateways);
    }

    protected List<EgressOnlyInternetGateway> listEgressOnlyInternetGateways(Ec2Client client) {
        DescribeEgressOnlyInternetGatewaysResponse describeEgressOnlyInternetGatewaysResponse = client.describeEgressOnlyInternetGateways();
        return describeEgressOnlyInternetGatewaysResponse.egressOnlyInternetGateways();
    }

    protected Maps<Resource> getResourceMaps(List<EgressOnlyInternetGateway> egressOnlyInternetGateways) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (EgressOnlyInternetGateway egressOnlyInternetGateway : egressOnlyInternetGateways) {

            List<InternetGatewayAttachment> internetGatewayAttachments = egressOnlyInternetGateway.attachments();

            for (InternetGatewayAttachment internetGatewayAttachment : internetGatewayAttachments) {
                resourceMapsBuilder.map(
                        Resource.builder()
                                .api("aws_egress_only_internet_gateway")
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

    TFImport getTFImport(List<EgressOnlyInternetGateway> egressOnlyInternetGateways) {
        return TFImport.builder()
                .importLines(egressOnlyInternetGateways.stream()
                        .map(egressOnlyInternetGateway -> TFImportLine.builder()
                                .address(MessageFormat.format("{0}.{1}",
                                        "aws_egress_only_internet_gateway",
                                        egressOnlyInternetGateway.egressOnlyInternetGatewayId()))
                                .id(egressOnlyInternetGateway.egressOnlyInternetGatewayId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
