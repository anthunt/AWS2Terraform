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
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;
import software.amazon.awssdk.services.ec2.model.InternetGatewayAttachment;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportEgressOnlyInternetGatewaysTest {

    private static ExportEgressOnlyInternetGateways exportEgressOnlyInternetGateways;

    @Autowired
    private ResourceLoader resourceLoader;

    private static Ec2Client client;
    @BeforeAll
    public static void beforeAll() {
        exportEgressOnlyInternetGateways = new ExportEgressOnlyInternetGateways();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getEc2Client();
    }

    private List<EgressOnlyInternetGateway> getEgressOnlyInternetGateways() {
        return List.of(
                EgressOnlyInternetGateway.builder()
                        .egressOnlyInternetGatewayId("eigw-015e0e244e24dfe8a")
                        .attachments(InternetGatewayAttachment.builder().vpcId("vpc-0a850bac9c765bfd5").build())
                        .tags(Tag.builder().key("Name").value("test").build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportEgressOnlyInternetGateways.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    void getResourceMaps() {
        // given
        List<EgressOnlyInternetGateway> internetGateways = getEgressOnlyInternetGateways();

        Maps<Resource> resourceMaps = exportEgressOnlyInternetGateways.getResourceMaps(internetGateways);
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/EgressOnlyInternetGateway.tf"));
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/EgressOnlyInternetGateway.cmd"));
        String actual = exportEgressOnlyInternetGateways.getTFImport(getEgressOnlyInternetGateways()).script();

        assertEquals(expected, actual);
    }
}