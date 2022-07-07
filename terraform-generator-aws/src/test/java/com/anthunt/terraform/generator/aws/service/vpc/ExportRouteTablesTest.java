package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSRoute;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSRouteTable;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSRouteTableAssociation;
import com.anthunt.terraform.generator.aws.support.DisabledOnNoAwsCredentials;
import com.anthunt.terraform.generator.aws.support.TestDataFileUtils;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Route;
import software.amazon.awssdk.services.ec2.model.RouteTableAssociation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportRouteTablesTest {

    private static ExportRouteTables exportRouteTables;

    @Autowired
    private ResourceLoader resourceLoader;

    private static Ec2Client client;

    @BeforeAll
    public static void beforeAll() {
        exportRouteTables = new ExportRouteTables();
        exportRouteTables.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getEc2Client();
    }

    private List<AWSRouteTable> getAwsRouteTables() {
        //noinspection unchecked
        return List.of(
                AWSRouteTable.builder()
                        .vpcId("vpc-7931b212")
                        .routeTableId("rtb-d6b5fdbd")
                        .awsRoutes(List.of(AWSRoute.builder()
                                        .routeTableId("rtb-d6b5fdbd")
                                        .route(Route.builder()
                                                .destinationCidrBlock("172.31.0.0/16")
                                                .gatewayId("local")
                                                .build())
                                        .build(),
                                AWSRoute.builder()
                                        .routeTableId("rtb-d6b5fdbd")
                                        .route(Route.builder()
                                                .destinationCidrBlock("0.0.0.0/0")
                                                .gatewayId("igw-8ecdbbe6")
                                                .build())
                                        .build()))
                        .awsRouteTableAssociation(AWSRouteTableAssociation.builder()
                                .routeTableAssociation(RouteTableAssociation.builder()
                                        .gatewayId("igw-8ecdbbe6")
                                        .routeTableId("rtb-d6b5fdbd")
                                        .build())
                                .build())
                        .tags(List.of())
                        .build(),
                AWSRouteTable.builder()
                        .vpcId("vpc-8931b212")
                        .routeTableId("rtb-e6b5fdbd")
                        .awsRoutes(List.of(AWSRoute.builder()
                                        .routeTableId("rtb-e6b5fdbd")
                                        .route(Route.builder()
                                                .destinationCidrBlock("172.31.0.0/16")
                                                .gatewayId("local")
                                                .build())
                                        .build(),
                                AWSRoute.builder()
                                        .routeTableId("rtb-e6b5fdbd")
                                        .route(Route.builder()
                                                .destinationCidrBlock("0.0.0.0/0")
                                                .gatewayId("igw-8ecdbbe6")
                                                .build())
                                        .build()))
                        .awsRouteTableAssociation(AWSRouteTableAssociation.builder()
                                .routeTableAssociation(RouteTableAssociation.builder()
                                        .subnetId("subnet-02c7511faa4344f83")
                                        .routeTableId("rtb-e6b5fdbd")
                                        .build())
                                .build())
                        .tags(List.of())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportRouteTables.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    void getResourceMaps() {
        // given
        List<AWSRouteTable> routeTables = getAwsRouteTables();

        Maps<Resource> resourceMaps = exportRouteTables.getResourceMaps(routeTables);
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/RouteTable.tf"));
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/RouteTable.cmd"));
        String actual = exportRouteTables.getTFImport(getAwsRouteTables()).script();

        assertEquals(expected, actual);
    }
}