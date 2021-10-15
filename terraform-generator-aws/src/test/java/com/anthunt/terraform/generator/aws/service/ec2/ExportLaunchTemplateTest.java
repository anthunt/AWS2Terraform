package com.anthunt.terraform.generator.aws.service.ec2;

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

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportLaunchTemplateTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportlaunchTemplates exportlaunchTemplates;

    private static Ec2Client client;

    @BeforeAll
    public static void beforeAll() {
        exportlaunchTemplates = new ExportlaunchTemplates();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getEc2Client();
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportlaunchTemplates.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void listLaunchTemplateVersions() {
        List<LaunchTemplateVersion> launchTemplateVersions = exportlaunchTemplates.listLaunchTemplateVersions(client);
        log.debug("launchTemplateVersions => {}", launchTemplateVersions);
    }

    @Test
    public void getResourceMaps() {
        List<LaunchTemplateVersion> awsEksClusters = List.of(
                LaunchTemplateVersion.builder()
                        .launchTemplateName("LT-DEV-DLS")
                        .launchTemplateData(ResponseLaunchTemplateData.builder()
                                .disableApiTermination(true)
                                .ebsOptimized(false)
                                .imageId("ami-08c64544f5cfcddd0")
                                .instanceInitiatedShutdownBehavior(ShutdownBehavior.STOP)
                                .instanceType(InstanceType.M5_XLARGE)
                                .keyName("DEV-KEYPAIR")
                                .iamInstanceProfile(builder -> builder.build())
                                .networkInterfaces(builder -> builder.associatePublicIpAddress(false))
                                .userData(Base64.getEncoder().encodeToString(
                                        TestDataFileUtils.asString(resourceLoader
                                                        .getResource("testData/aws/input/LaunchTemplateUserData.txt"))
                                                .getBytes()
                                ))
                                .build())
                        .build()
        );

        Maps<Resource> resourceMaps = exportlaunchTemplates.getResourceMaps(awsEksClusters);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/LaunchTemplate.tf")
        );
        assertEquals(expected, actual);
    }

}