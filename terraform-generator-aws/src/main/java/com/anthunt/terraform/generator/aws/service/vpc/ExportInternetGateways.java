package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSInternetGateway;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInternetGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.InternetGateway;
import software.amazon.awssdk.services.ec2.model.InternetGatewayAttachment;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportInternetGateways extends AbstractExport<Ec2Client> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "InternetGateways";

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSInternetGateway> internetGateways = listAwsInternetGateways(client);
        return getResourceMaps(internetGateways);
    }

    @Override
    protected TFImport scriptImport(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSInternetGateway> awsInternetGateways = listAwsInternetGateways(client);
        return getTFImport(awsInternetGateways);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    protected List<AWSInternetGateway> listAwsInternetGateways(Ec2Client client) {
        DescribeInternetGatewaysResponse describeInternetGatewaysResponse = client.describeInternetGateways();
        return describeInternetGatewaysResponse.internetGateways().stream()
                .map(internetGateway -> AWSInternetGateway.builder()
                        .internetGateway(internetGateway)
                        .build())
                .collect(Collectors.toList());
    }

    protected Maps<Resource> getResourceMaps(List<AWSInternetGateway> awsInternetGateways) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSInternetGateway awsInternetGateway : awsInternetGateways) {

            InternetGateway internetGateway = awsInternetGateway.getInternetGateway();
            List<InternetGatewayAttachment> internetGatewayAttachments = internetGateway.attachments();

            for (InternetGatewayAttachment internetGatewayAttachment : internetGatewayAttachments) {
                resourceMapsBuilder.map(
                        Resource.builder()
                                .api(awsInternetGateway.getTerraformResourceName())
                                .name(awsInternetGateway.getResourceName())
                                .argument("vpc_id", TFString.build(internetGatewayAttachment.vpcId()))
                                .argument("tags", TFMap.build(
                                        internetGateway.tags().stream()
                                                .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                ))
                                .build()
                );
            }
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSInternetGateway> awsInternetGateways) {
        return TFImport.builder()
                .importLines(awsInternetGateways.stream()
                        .map(awsInternetGateway -> TFImportLine.builder()
                                .address(awsInternetGateway.getTerraformAddress())
                                .id(awsInternetGateway.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
