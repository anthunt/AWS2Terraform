package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSSubnet;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFBool;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
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

    private static final String DEFAULT_OUTPUT_FILE_NAME = "Subnets";

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSSubnet> awsSubnets = listAwsSubnets(client);
        return getResourceMaps(awsSubnets);
    }

    @Override
    protected TFImport scriptImport(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSSubnet> awsSubnets = listAwsSubnets(client);
        return getTFImport(awsSubnets);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSSubnet> listAwsSubnets(Ec2Client client) {
        DescribeSubnetsResponse describeSubnetsResponse = client.describeSubnets();
        return describeSubnetsResponse.subnets().stream()
                .map(subnet -> AWSSubnet.builder()
                        .subnet(subnet)
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSSubnet> awsSubnets) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSSubnet awsSubnet : awsSubnets) {
            Subnet subnet = awsSubnet.getSubnet();
            log.debug("subnet => {}", subnet);

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api(awsSubnet.getTerraformResourceName())
                            .name(awsSubnet.getResourceName())
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
            );
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSSubnet> awsSubnets) {
        return TFImport.builder()
                .importLines(awsSubnets.stream()
                        .map(awsSubnet -> TFImportLine.builder()
                                .address(awsSubnet.getTerraformAddress())
                                .id(awsSubnet.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
