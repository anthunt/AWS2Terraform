package com.anthunt.terraform.generator.aws.service.cloudwatchlogs;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.cloudwatchlogs.model.AWSLogGroup;
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
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportCloudwatchLogGroupsTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportCloudWatchLogGroups exportCloudwatchLogGroups;

    private static CloudWatchLogsClient client;

    @BeforeAll
    public static void beforeAll() {
        exportCloudwatchLogGroups = new ExportCloudWatchLogGroups();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getCloudWatchLogGroupClient();
    }

    private List<AWSLogGroup> getAwsLogGroups() {
        return List.of(
                AWSLogGroup.builder()
                        .logGroup(LogGroup.builder().logGroupName("/aws/containerinsights/EKS-CLS-SAMPLE/application")
                                .build())
                        .tag("Name", "test log group")
                        .build(),
                AWSLogGroup.builder()
                        .logGroup(LogGroup.builder().logGroupName("/aws/containerinsights/EKS-CLS-SAMPLE/dataplane")
                                .build())
                        .build(),
                AWSLogGroup.builder()
                        .logGroup(LogGroup.builder().logGroupName("/aws/containerinsights/EKS-CLS-SAMPLE/host")
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportCloudwatchLogGroups.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        List<AWSLogGroup> awsLogGroups = getAwsLogGroups();

        Maps<Resource> resourceMaps = exportCloudwatchLogGroups.getResourceMaps(awsLogGroups);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/CloudWatchLog.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/CloudWatchLog.cmd"));
        String actual = exportCloudwatchLogGroups.getTFImport(getAwsLogGroups()).script();

        assertEquals(expected, actual);
    }

}