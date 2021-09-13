package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
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
import software.amazon.awssdk.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportSubnetsTest {

    private static ExportSubnets exportSubnets;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportSubnets = new ExportSubnets();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();

        Maps<Resource> export = exportSubnets.export(ec2Client, null, null);

        log.debug("result => {}", export.unmarshall());
    }

    @Test
    void getResourceMaps() {
        // given
        List<Subnet> subnets = List.of(
                Subnet.builder()
                        .availabilityZoneId("apne2-az2")
                        .cidrBlock("172.31.16.0/20")
                        .mapPublicIpOnLaunch(true)
                        .assignIpv6AddressOnCreation(false)
                        .vpcId("vpc-7931b212")
                        .build(),
                Subnet.builder()
                        .availabilityZoneId("apne2-az1")
                        .cidrBlock("172.31.0.0/20")
                        .mapPublicIpOnLaunch(true)
                        .assignIpv6AddressOnCreation(false)
                        .vpcId("vpc-7931b212")
                        .build(),
                Subnet.builder()
                        .availabilityZoneId("apne2-az3")
                        .cidrBlock("172.31.32.0/20")
                        .mapPublicIpOnLaunch(true)
                        .assignIpv6AddressOnCreation(false)
                        .vpcId("vpc-7931b212")
                    .build()
            );

        Maps<Resource> resourceMaps = exportSubnets.getResourceMaps(subnets);
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/vpc/expected/Subnet.tf"));
        assertEquals(expected, actual);
    }
}