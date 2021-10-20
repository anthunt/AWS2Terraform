package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
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
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportRouteTables extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<RouteTable> routeTables = listRouteTables(client);
        return getResourceMaps(routeTables);
    }

    @Override
    protected TFImport scriptImport(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<RouteTable> routeTables = listRouteTables(client);
        return getTFImport(routeTables);
    }

    protected List<RouteTable> listRouteTables(Ec2Client client) {
        DescribeRouteTablesResponse describeRouteTablesResponse = client.describeRouteTables();
        return describeRouteTablesResponse.routeTables();
    }

    protected Maps<Resource> getResourceMaps(List<RouteTable> routeTables) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        for (RouteTable routeTable : routeTables) {

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_route_table")
                            .name(routeTable.routeTableId())
                            .argument("vpc_id", TFString.build(routeTable.vpcId()))
                            .argument("tags", TFMap.build(
                                    routeTable.tags().stream()
                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                            ))
                            .argument("propagating_vgws", TFList.build(
                                    routeTable.propagatingVgws().stream()
                                            .map(vgw -> TFString.build(vgw.gatewayId()))
                                            .collect(Collectors.toCollection(ArrayList::new))
                            ))
                            .build()
            );

            List<Route> routes = routeTable.routes();
            for (Route route : routes) {
                resourceMapsBuilder.map(
                        Resource.builder()
                                .api("aws_route")
                                .name(getRouteResourceName(routeTable.routeTableId(), route))
                                .argument("route_table_id", TFString.build(routeTable.routeTableId()))
                                .argumentIf(route.destinationCidrBlock() != null, "destination_cidr_block", TFString.build(route.destinationCidrBlock()))
                                .argumentIf(route.destinationIpv6CidrBlock() != null, "destination_ipv6_cidr_block", TFString.build(route.destinationIpv6CidrBlock()))
                                .argumentIf(route.egressOnlyInternetGatewayId() != null, "egress_only_gateway_id", TFString.build(route.egressOnlyInternetGatewayId()))
                                .argumentIf(route.gatewayId() != null, "gateway_id", TFString.build(route.gatewayId()))
                                .argumentIf(route.instanceId() != null, "instance_id", TFString.build(route.instanceId()))
                                .argumentIf(route.natGatewayId() != null, "nat_gateway_id", TFString.build(route.natGatewayId()))
                                .argumentIf(route.localGatewayId() != null, "local_gateway_id", TFString.build(route.localGatewayId()))
                                .argumentIf(route.networkInterfaceId() != null, "network_interface_id", TFString.build(route.networkInterfaceId()))
                                .argumentIf(route.transitGatewayId() != null, "transit_gateway_id", TFString.build(route.transitGatewayId()))
                                .argumentIf(route.destinationPrefixListId() != null, "vpc_endpoint_id", TFString.build(route.destinationPrefixListId()))
                                .argumentIf(route.vpcPeeringConnectionId() != null, "vpc_peering_connection_id", TFString.build(route.vpcPeeringConnectionId()))
                                .build()
                );
            }

            routeTable.associations().forEach(routeTableAssociation ->
                    resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_route_table_association")
                                    .name(getRouteTableAssociationResourceName(routeTableAssociation.subnetId(),
                                            routeTableAssociation.gatewayId(),
                                            routeTable.routeTableId()))
                                    .argumentIf(Optional.ofNullable(routeTableAssociation.subnetId()).isPresent(),
                                            "subnet_id",
                                            () -> TFString.build(routeTableAssociation.subnetId()))
                                    .argumentIf(Optional.ofNullable(routeTableAssociation.gatewayId()).isPresent(),
                                            "gateway_id",
                                            () -> TFString.build(routeTableAssociation.gatewayId()))
                                    .argument("route_table_id", TFString.build(routeTableAssociation.routeTableId()))
                                    .build()
                    )
            );
        }
        return resourceMapsBuilder.build();
    }

    private String getRouteTableAssociationResourceName(String subnetId, String gatewayId, String routeTableId) {
        return getRouteTableAssociationResourceId(subnetId, gatewayId, routeTableId).replaceAll("/", "-");
    }

    private String getRouteTableAssociationResourceId(String subnetId, String gatewayId, String routeTableId) {
        return MessageFormat.format("{0}/{1}", Optional.ofNullable(subnetId).orElse(gatewayId), routeTableId);
    }

    TFImport getTFImport(List<RouteTable> routeTables) {
        TFImport.TFImportBuilder tfImportBuilder = TFImport.builder();
        for (RouteTable routeTable : routeTables) {
            tfImportBuilder.importLine(
                    TFImportLine.builder()
                            .address(MessageFormat.format("{0}.{1}",
                                    "aws_route_table",
                                    routeTable.routeTableId()))
                            .id(routeTable.routeTableId())
                            .build()
            );
            routeTable.routes().forEach(route ->
                    tfImportBuilder.importLine(
                            TFImportLine.builder()
                                    .address(MessageFormat.format("{0}.{1}",
                                            "aws_route",
                                            getRouteResourceName(routeTable.routeTableId(), route)))
                                    .id(getRouteResourceId(routeTable.routeTableId(), route))
                                    .build()
                    )
            );
            routeTable.associations().forEach(routeTableAssociation ->
                    tfImportBuilder.importLine(
                            TFImportLine.builder()
                                    .address(MessageFormat.format("{0}.{1}",
                                            "aws_route_table_association",
                                            getRouteTableAssociationResourceName(routeTableAssociation.subnetId(),
                                                    routeTableAssociation.gatewayId(),
                                                    routeTable.routeTableId())))
                                    .id(getRouteTableAssociationResourceId(
                                            routeTableAssociation.subnetId(),
                                            routeTableAssociation.gatewayId(),
                                            routeTableAssociation.routeTableId()))
                                    .build()
                    )
            );
        }
        return tfImportBuilder.build();
    }

    private String getRouteResourceName(String routeTableId, Route route) {
        return getRouteResourceId(routeTableId, route)
                .replaceAll("\\.", "-")
                .replaceAll("/", "_");
    }

    private String getRouteResourceId(String routeTableId, Route route) {
        return MessageFormat.format("{0}_{1}",
                routeTableId,
                Optional.ofNullable(route.destinationCidrBlock())
                        .orElse(Optional.ofNullable(route.destinationIpv6CidrBlock())
                                .orElse(route.destinationPrefixListId())));
    }
}
