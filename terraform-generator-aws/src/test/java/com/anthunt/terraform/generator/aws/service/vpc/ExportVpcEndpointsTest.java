package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSVpcEndpoint;
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
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportVpcEndpointsTest {

    private static ExportVpcEndpoints exportVpcEndpoints;

    @Autowired
    private ResourceLoader resourceLoader;

    private static Ec2Client client;

    @BeforeAll
    public static void beforeAll() {
        exportVpcEndpoints = new ExportVpcEndpoints();
        AmazonClients amazonClients = AmazonClients.builder().region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getEc2Client();
    }

    private List<AWSVpcEndpoint> getVpcEndpoints() {
        return List.of(
                AWSVpcEndpoint.builder()
                        .vpcEndpoint(VpcEndpoint.builder()
                                .vpcEndpointId("vpce-00012b7ff8c5d5a426")
                                .vpcId("vpc-a01106c2")
                                .serviceName("com.amazonaws.ap-northeast-2.s3")
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportVpcEndpoints.export(client, null, null);
        log.debug("result => {}", export.unmarshall());
    }

    @Test
    void getResourceMaps() {
        Maps<Resource> resourceMaps = exportVpcEndpoints.getResourceMaps(getVpcEndpoints());
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/VpcEndpoint.tf"));
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/VpcEndpoint.cmd"));
        String actual = exportVpcEndpoints.getTFImport(getVpcEndpoints()).script();

        assertEquals(expected, actual);
    }
}