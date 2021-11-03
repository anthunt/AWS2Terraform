package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSVpc;
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
import software.amazon.awssdk.services.ec2.model.Vpc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportVpcsTest {

    private static ExportVpcs exportvpcs;

    @Autowired
    private ResourceLoader resourceLoader;

    private static Ec2Client client;

    @BeforeAll
    public static void beforeAll() {
        exportvpcs = new ExportVpcs();
        exportvpcs.setDelayBetweenApis(0);
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getEc2Client();
    }

    private List<AWSVpc> getAwsVpcs() {
        return List.of(
                AWSVpc.builder()
                        .vpc(Vpc.builder()
                                .vpcId("vpc-a01106c2")
                                .cidrBlock("172.31.0.0/16")
                                .instanceTenancy("default")
                                .build())
                        .enableDnsSupport(true)
                        .enableDnsHostnames(true)
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {

        Maps<Resource> export = exportvpcs.export(client, null, null);

        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    void getResourceMaps() {
        // given
        List<AWSVpc> vpcs = getAwsVpcs();

        Maps<Resource> resourceMaps = exportvpcs.getResourceMaps(vpcs);
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/Vpc.tf"));
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/Vpc.cmd"));
        String actual = exportvpcs.getTFImport(getAwsVpcs()).script();

        assertEquals(expected, actual);
    }
}