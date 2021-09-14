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
import software.amazon.awssdk.services.ec2.model.InternetGateway;
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

    @BeforeAll
    public static void beforeAll() {
        exportEgressOnlyInternetGateways = new ExportEgressOnlyInternetGateways();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();

        Maps<Resource> export = exportEgressOnlyInternetGateways.export(ec2Client, null, null);

        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    void getResourceMaps() {
        // given
        List<EgressOnlyInternetGateway> internetGateways = List.of(
                EgressOnlyInternetGateway.builder()
                        .attachments(InternetGatewayAttachment.builder().vpcId("vpc-0a850bac9c765bfd5").build())
                        .tags(Tag.builder().key("Name").value("test").build())
                        .build()
            );

        Maps<Resource> resourceMaps = exportEgressOnlyInternetGateways.getResourceMaps(internetGateways);
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/vpc/expected/EgressOnlyInternetGateway.tf"));
        assertEquals(expected, actual);
    }
}