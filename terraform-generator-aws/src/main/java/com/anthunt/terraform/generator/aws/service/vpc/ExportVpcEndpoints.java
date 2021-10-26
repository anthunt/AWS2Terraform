package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSVpcEndpoint;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFExpression;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeVpcEndpointsResponse;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportVpcEndpoints extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSVpcEndpoint> awsVpcEndpoints = listVpcEndpoints(client);
        return getResourceMaps(awsVpcEndpoints);
    }

    @Override
    protected TFImport scriptImport(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSVpcEndpoint> awsVpcEndpoints = listVpcEndpoints(client);
        return getTFImport(awsVpcEndpoints);
    }

    protected List<AWSVpcEndpoint> listVpcEndpoints(Ec2Client client) {
        DescribeVpcEndpointsResponse describeVpcEndpointsResponse = client.describeVpcEndpoints();
        return describeVpcEndpointsResponse.vpcEndpoints().stream()
                .map(vpcEndpoint -> AWSVpcEndpoint.builder()
                        .vpcEndpoint(vpcEndpoint).build())
                .collect(Collectors.toList());
    }

    protected Maps<Resource> getResourceMaps(List<AWSVpcEndpoint> awsVpcEndpoints) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();


        awsVpcEndpoints.forEach(awsVpcEndpoint -> {
                    VpcEndpoint vpcEndpoint = awsVpcEndpoint.getVpcEndpoint();
                    resourceMapsBuilder.map(
                            Resource.builder()
                                    .api(awsVpcEndpoint.getTerraformResourceName())
                                    .name(awsVpcEndpoint.getResourceName())
                                    .argument("vpc_id", TFExpression.build(
                                            MessageFormat.format("aws_vpc.{0}.id", vpcEndpoint.vpcId())))
                                    .argument("service_name", TFString.build(vpcEndpoint.serviceName()))
                                    .argument("tags", TFMap.build(
                                            vpcEndpoint.tags().stream()
                                                    .collect(Collectors.toMap(Tag::key,
                                                            tag -> TFString.build(tag.value())))
                                    ))
                                    .build()
                    );
                }
        );
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSVpcEndpoint> awsVpcEndpoints) {
        TFImport.TFImportBuilder tfImportBuilder = TFImport.builder();
        awsVpcEndpoints.forEach(awsVpcEndpoint -> tfImportBuilder.importLine(
                TFImportLine.builder()
                        .address(awsVpcEndpoint.getTerraformAddress())
                        .id(awsVpcEndpoint.getResourceId())
                        .build()
        ));
        return tfImportBuilder.build();
    }
}
