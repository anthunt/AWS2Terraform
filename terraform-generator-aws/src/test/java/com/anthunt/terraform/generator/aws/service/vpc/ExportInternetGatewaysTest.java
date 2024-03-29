package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSInternetGateway;
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
import software.amazon.awssdk.services.ec2.model.InternetGateway;
import software.amazon.awssdk.services.ec2.model.InternetGatewayAttachment;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportInternetGatewaysTest {

    private static ExportInternetGateways exportInternetGateways;

    @Autowired
    private ResourceLoader resourceLoader;

    private static Ec2Client client;

    @BeforeAll
    public static void beforeAll() {
        exportInternetGateways = new ExportInternetGateways();
        exportInternetGateways.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getEc2Client();
    }

    private List<AWSInternetGateway> getAwsInternetGateways() {
        return List.of(
                AWSInternetGateway.builder()
                        .internetGateway(InternetGateway.builder()
                                .internetGatewayId("igw-c0a643a9")
                                .attachments(InternetGatewayAttachment.builder().vpcId("vpc-0a850bac9c765bfd5").build())
                                .tags(Tag.builder().key("Name").value("test").build())
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportInternetGateways.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    void getResourceMaps() {
        // given
        List<AWSInternetGateway> internetGateways = getAwsInternetGateways();

        Maps<Resource> resourceMaps = exportInternetGateways.getResourceMaps(internetGateways);
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/InternetGateway.tf"));
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/InternetGateway.cmd"));
        String actual = exportInternetGateways.getTFImport(getAwsInternetGateways()).script();

        assertEquals(expected, actual);
    }
}