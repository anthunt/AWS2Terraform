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
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportVpcs extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        DescribeVpcsResponse response = client.describeVpcs();
        List<Vpc> vpcs = response.vpcs();

        int i = 0;
        for (Vpc vpc : vpcs) {

            DescribeVpcAttributeResponse describeVpcAttributeResponse = client.describeVpcAttribute(
                    DescribeVpcAttributeRequest.builder()
                            .vpcId(vpc.vpcId())
                            .build()
            );

            resourceMapsBuilder.map(
                    Resource.builder()
                        .api("aws_vpc")
                        .name("vpc" + i)
                        .arguments(
                                TFArguments.builder()
                                        .argument("cidr_block", TFString.build(vpc.cidrBlock()))
                                        .argument("instance_tenancy", TFString.build(vpc.instanceTenancyAsString()))
                                        .argument("enable_dns_support", TFBool.build(describeVpcAttributeResponse.enableDnsSupport().value()))
                                        .argument("enable_dns_hostnames", TFBool.build(describeVpcAttributeResponse.enableDnsHostnames().value()))
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
