package com.anthunt.terraform.generator.aws.service.ec2;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.ec2.model.AWSLaunchTemplateVersion;
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
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.LaunchTemplateVersion;
import software.amazon.awssdk.services.ec2.model.ResponseLaunchTemplateData;
import software.amazon.awssdk.services.ec2.model.ShutdownBehavior;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportLaunchTemplateTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportLaunchTemplates exportlaunchTemplates;

    private static Ec2Client client;

    @BeforeAll
    public static void beforeAll() {
        exportlaunchTemplates = new ExportLaunchTemplates();
        exportlaunchTemplates.setDelayBetweenApis(0);
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getEc2Client();
    }

    private List<AWSLaunchTemplateVersion> getAwsLaunchTemplateVersions() {
        return List.of(
                AWSLaunchTemplateVersion.builder()
                        .launchTemplateVersion(LaunchTemplateVersion.builder()
                                .launchTemplateName("LT-DEV-DLS")
                                .launchTemplateId("lt-0c6296fcff64943d6")
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
                                .build())
                        .build()

        );
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
        List<AWSLaunchTemplateVersion> awsLaunchTemplateVersions = exportlaunchTemplates.listAwsLaunchTemplateVersion(client);
        log.debug("awsLaunchTemplateVersions => {}", awsLaunchTemplateVersions);
    }

    @Test
    public void getResourceMaps() {
        List<AWSLaunchTemplateVersion> awsLaunchTemplateVersions = getAwsLaunchTemplateVersions();
        Maps<Resource> resourceMaps = exportlaunchTemplates.getResourceMaps(awsLaunchTemplateVersions);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/LaunchTemplate.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/LaunchTemplate.cmd"));
        String actual = exportlaunchTemplates.getTFImport(getAwsLaunchTemplateVersions()).script();

        assertEquals(expected, actual);
    }

}