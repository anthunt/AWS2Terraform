package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSRoute;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSRouteTable;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSRouteTableAssociation;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFList;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.Route;
import software.amazon.awssdk.services.ec2.model.RouteTableAssociation;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportRouteTables extends AbstractExport<Ec2Client> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "RouteTables";

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRouteTable> awsRouteTables = listAwsRouteTables(client);
        return getResourceMaps(awsRouteTables);
    }

    @Override
    protected TFImport scriptImport(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRouteTable> awsRouteTables = listAwsRouteTables(client);
        return getTFImport(awsRouteTables);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    protected List<AWSRouteTable> listAwsRouteTables(Ec2Client client) {
        DescribeRouteTablesResponse describeRouteTablesResponse = client.describeRouteTables();
        return describeRouteTablesResponse.routeTables().stream()
                .map(routeTable -> AWSRouteTable.builder()
                        .vpcId(routeTable.vpcId())
                        .routeTableId(routeTable.routeTableId())
                        .awsRoutes(routeTable.routes().stream()
                                .map(route -> AWSRoute.builder()
                                        .route(route)
                                        .routeTableId(routeTable.routeTableId())
                                        .build())
                                .collect(Collectors.toList()))
                        .awsRouteTableAssociations(routeTable.associations().stream()
                                .map(routeTableAssociation -> AWSRouteTableAssociation.builder()
                                        .routeTableAssociation(routeTableAssociation)
                                        .build())
                                .collect(Collectors.toList()))
                        .tags(routeTable.tags())
                        .propagatingVgws(routeTable.propagatingVgws())
                        .build())
                .collect(Collectors.toList());
    }

    protected Maps<Resource> getResourceMaps(List<AWSRouteTable> awsRouteTables) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        for (AWSRouteTable awsRouteTable : awsRouteTables) {
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api(awsRouteTable.getTerraformResourceName())
                            .name(awsRouteTable.getResourceName())
                            .argument("vpc_id", TFString.build(awsRouteTable.getVpcId()))
                            .argument("tags",
                                    TFMap.build(
                                            awsRouteTable.getTags().stream()
                                                    .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                    ))
                            .argument("propagating_vgws", TFList.build(
                                    awsRouteTable.getPropagatingVgws().stream()
                                            .map(vgw -> TFString.build(vgw.gatewayId()))
                                            .collect(Collectors.toCollection(ArrayList::new))
                            ))
                            .build()
            );

            List<AWSRoute> awsRoutes = awsRouteTable.getAwsRoutes();
            for (AWSRoute awsRoute : awsRoutes) {
                Route route = awsRoute.getRoute();
                resourceMapsBuilder.map(
                        Resource.builder()
                                .api(awsRoute.getTerraformResourceName())
                                .name(awsRoute.getResourceName())
                                .argument("route_table_id", TFString.build(awsRoute.getRouteTableId()))
                                .argumentIf(Optional.ofNullable(route.destinationCidrBlock()).isPresent(),
                                        "destination_cidr_block",
                                        () -> TFString.build(route.destinationCidrBlock()))
                                .argumentIf(Optional.ofNullable(route.destinationIpv6CidrBlock()).isPresent(),
                                        "destination_ipv6_cidr_block",
                                        () -> TFString.build(route.destinationIpv6CidrBlock()))
                                .argumentIf(Optional.ofNullable(route.egressOnlyInternetGatewayId()).isPresent(),
                                        "egress_only_gateway_id",
                                        () -> TFString.build(route.egressOnlyInternetGatewayId()))
                                .argumentIf(Optional.ofNullable(route.gatewayId()).isPresent(),
                                        "gateway_id",
                                        () -> TFString.build(route.gatewayId()))
                                .argumentIf(Optional.ofNullable(route.instanceId()).isPresent(),
                                        "instance_id",
                                        () -> TFString.build(route.instanceId()))
                                .argumentIf(Optional.ofNullable(route.natGatewayId()).isPresent(),
                                        "nat_gateway_id",
                                        () -> TFString.build(route.natGatewayId()))
                                .argumentIf(Optional.ofNullable(route.localGatewayId()).isPresent(),
                                        "local_gateway_id",
                                        () -> TFString.build(route.localGatewayId()))
                                .argumentIf(Optional.ofNullable(route.networkInterfaceId()).isPresent(),
                                        "network_interface_id",
                                        () -> TFString.build(route.networkInterfaceId()))
                                .argumentIf(Optional.ofNullable(route.transitGatewayId()).isPresent(),
                                        "transit_gateway_id",
                                        () -> TFString.build(route.transitGatewayId()))
                                .argumentIf(Optional.ofNullable(route.destinationPrefixListId()).isPresent(),
                                        "vpc_endpoint_id",
                                        () -> TFString.build(route.destinationPrefixListId()))
                                .argumentIf(Optional.ofNullable(route.vpcPeeringConnectionId()).isPresent(),
                                        "vpc_peering_connection_id",
                                        () -> TFString.build(route.vpcPeeringConnectionId()))
                                .build()
                );
            }

            awsRouteTable.getAwsRouteTableAssociations().forEach(awsRouteTableAssociation -> {
                        RouteTableAssociation routeTableAssociation = awsRouteTableAssociation.getRouteTableAssociation();
                        resourceMapsBuilder.map(
                                Resource.builder()
                                        .api(awsRouteTableAssociation.getTerraformResourceName())
                                        .name(awsRouteTableAssociation.getResourceName())
                                        .argumentIf(Optional.ofNullable(routeTableAssociation.subnetId()).isPresent(),
                                                "subnet_id",
                                                () -> TFString.build(routeTableAssociation.subnetId()))
                                        .argumentIf(Optional.ofNullable(routeTableAssociation.gatewayId()).isPresent(),
                                                "gateway_id",
                                                () -> TFString.build(routeTableAssociation.gatewayId()))
                                        .argument("route_table_id", TFString.build(routeTableAssociation.routeTableId()))
                                        .build()
                        );
                    }
            );
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSRouteTable> awsRouteTables) {
        TFImport.TFImportBuilder tfImportBuilder = TFImport.builder();
        for (AWSRouteTable awsRouteTable : awsRouteTables) {
            tfImportBuilder.importLine(
                    TFImportLine.builder()
                            .address(awsRouteTable.getTerraformAddress())
                            .id(awsRouteTable.getResourceId())
                            .build()
            );
            awsRouteTable.getAwsRoutes().forEach(awsRoute -> tfImportBuilder.importLine(
                            TFImportLine.builder()
                                    .address(awsRoute.getTerraformAddress())
                                    .id(awsRoute.getResourceId())
                                    .build()
                    )
            );
            awsRouteTable.getAwsRouteTableAssociations().forEach(awsRouteTableAssociation ->
                    tfImportBuilder.importLine(
                            TFImportLine.builder()
                                    .address(awsRouteTableAssociation.getTerraformAddress())
                                    .id(awsRouteTableAssociation.getResourceId())
                                    .build()
                    )
            );
        }
        return tfImportBuilder.build();
    }
}
