package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFList;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.Route;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportRouteTables extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<RouteTable> routeTables = getRouteTables(client);

        return getResourceMaps(routeTables);
    }

    protected List<RouteTable> getRouteTables(Ec2Client client) {
        DescribeRouteTablesResponse describeRouteTablesResponse = client.describeRouteTables();
        return describeRouteTablesResponse.routeTables();
    }

    protected Maps<Resource> getResourceMaps(List<RouteTable> routeTables) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        int i = 0;
        for(RouteTable routeTable : routeTables) {

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_route_table")
                            .name("route_table" + i)
                            .arguments(
                                    TFArguments.builder()
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
                            ).build()
            );

            List<Route> routes = routeTable.routes();
            int j = 0;
            for(Route route : routes) {

                resourceMapsBuilder.map(
                        Resource.builder()
                                .api("aws_route")
                                .name("route_table" + i + ".route" + j)
                                .arguments(
                                        TFArguments.builder()
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
                                )
                                .build()
                );

                j++;
            }
            i++;
        }

        return resourceMapsBuilder.build();
    }

}
