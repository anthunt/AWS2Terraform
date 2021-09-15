package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.vpc.model.VpcDto;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFBool;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportVpcs extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<VpcDto> vpcDtos = getVpcs(client);
        return getResourceMaps(vpcDtos);
    }

    List<VpcDto> getVpcs(Ec2Client client) {
        DescribeVpcsResponse response = client.describeVpcs();
        return response.vpcs().stream().map(vpc ->
            VpcDto.builder()
                    .vpc(vpc)
                    .enableDnsSupport(client.describeVpcAttribute(
                            DescribeVpcAttributeRequest.builder()
                                    .vpcId(vpc.vpcId())
                                    .attribute(VpcAttributeName.ENABLE_DNS_SUPPORT)
                                    .build()).enableDnsSupport().value())
                    .enableDnsHostnames(client.describeVpcAttribute(
                            DescribeVpcAttributeRequest.builder()
                                    .vpcId(vpc.vpcId())
                                    .attribute(VpcAttributeName.ENABLE_DNS_HOSTNAMES)
                                    .build()).enableDnsHostnames().value())
                    .build()
        ).collect(Collectors.toList());

    }

    Maps<Resource> getResourceMaps(List<VpcDto> vpcDtos) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        int i = 0;
        for (VpcDto vpcDto : vpcDtos) {
            Vpc vpc = vpcDto.getVpc();
            resourceMapsBuilder.map(
                    Resource.builder()
                        .api("aws_vpc")
                        .name("vpc" + i)
                        .arguments(
                                TFArguments.builder()
                                        .argument("cidr_block", TFString.build(vpc.cidrBlock()))
                                        .argument("instance_tenancy", TFString.build(vpc.instanceTenancyAsString()))
                                        .argument("enable_dns_support", TFBool.build(vpcDto.isEnableDnsSupport()))
                                        .argument("enable_dns_hostnames", TFBool.build(vpcDto.isEnableDnsHostnames()))
                                        .argument("enable_classiclink", TFBool.build(false))
                                        .argument("assign_generated_ipv6_block", TFBool.build(vpc.hasIpv6CidrBlockAssociationSet()))
                                        .argument("tags", TFMap.build(
                                                vpc.tags().stream()
                                                    .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                        ))
                                        .build()
                        )
                    .build()
            );

            i++;
        }

        return resourceMapsBuilder.build();
    }

}
