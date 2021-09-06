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

        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        DescribeRouteTablesResponse describeRouteTablesResponse = client.describeRouteTables();
        List<RouteTable> routeTables = describeRouteTablesResponse.routeTables();

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
                TFArguments.TFArgumentsBuilder tfArgumentsBuilder = TFArguments.builder();

                tfArgumentsBuilder.argument("route_table_id", TFString.build(routeTable.routeTableId()));
                if(route.destinationCidrBlock() != null) {
                    tfArgumentsBuilder.argument("destination_cidr_block", TFString.build(route.destinationCidrBlock()));
                }
                if(route.destinationIpv6CidrBlock() != null) {
                    tfArgumentsBuilder.argument("destination_ipv6_cidr_block", TFString.build(route.destinationIpv6CidrBlock()));
                }
                if(route.egressOnlyInternetGatewayId() != null) {
                    tfArgumentsBuilder.argument("egress_only_gateway_id", TFString.build(route.egressOnlyInternetGatewayId()));
                }
                if(route.gatewayId() != null) {
                    tfArgumentsBuilder.argument("gateway_id", TFString.build(route.gatewayId()));
                }
                if(route.instanceId() != null) {
                    tfArgumentsBuilder.argument("instance_id", TFString.build(route.instanceId()));
                }
                if(route.natGatewayId() != null) {
                    tfArgumentsBuilder.argument("nat_gateway_id", TFString.build(route.natGatewayId()));
                }
                if(route.localGatewayId() != null) {
                    tfArgumentsBuilder.argument("local_gateway_id", TFString.build(route.localGatewayId()));
                }
                if(route.networkInterfaceId() != null) {
                    tfArgumentsBuilder.argument("network_interface_id", TFString.build(route.networkInterfaceId()));
                }
                if(route.transitGatewayId() != null) {
                    tfArgumentsBuilder.argument("transit_gateway_id", TFString.build(route.transitGatewayId()));
                }
                if(route.destinationPrefixListId() != null) {
                    tfArgumentsBuilder.argument("vpc_endpoint_id", TFString.build(route.destinationPrefixListId()));
                }
                if(route.vpcPeeringConnectionId() != null) {
                    tfArgumentsBuilder.argument("vpc_peering_connection_id", TFString.build(route.vpcPeeringConnectionId()));
                }

                resourceMapsBuilder.map(
                        Resource.builder()
                                .api("aws_route")
                                .name("route_table" + i + ".route" + j)
                                .arguments(tfArgumentsBuilder.build())
                                .build()
                );

                j++;
            }
            i++;
        }

        return resourceMapsBuilder.build();
    }

}
