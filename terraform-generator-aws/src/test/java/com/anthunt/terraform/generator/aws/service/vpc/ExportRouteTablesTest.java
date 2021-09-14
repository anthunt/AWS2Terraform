package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.vpc.dto.VpcDto;
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
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.Vpc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportRouteTablesTest {

    private static ExportRouteTables exportRouteTables;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportRouteTables = new ExportRouteTables();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();

        Maps<Resource> export = exportRouteTables.export(ec2Client, null, null);

        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    void getResourceMaps() {
        // given
        List<RouteTable> routeTables = List.of(
                RouteTable.builder()
                        .vpcId("vpc-7931b212")
                        .routeTableId("rtb-d6b5fdbd")
                        .routes(Route.builder()
                                        .destinationCidrBlock("172.31.0.0/16")
                                        .gatewayId("local")
                                        .build(),
                                Route.builder()
                                        .destinationCidrBlock("0.0.0.0/0")
                                        .gatewayId("igw-8ecdbbe6")
                                        .build())
                        .build()
            );

        Maps<Resource> resourceMaps = exportRouteTables.getResourceMaps(routeTables);
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/vpc/expected/RouteTable.tf"));
        assertEquals(expected, actual);
    }
}