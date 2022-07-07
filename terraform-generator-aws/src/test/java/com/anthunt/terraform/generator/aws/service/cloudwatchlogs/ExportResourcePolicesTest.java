package com.anthunt.terraform.generator.aws.service.cloudwatchlogs;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.cloudwatchlogs.model.AWSResourcePolicy;
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
import software.amazon.awssdk.services.cloudwatchlogs.model.ResourcePolicy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportResourcePolicesTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportResourcePolicies exportResourcePolicies;

    private static CloudWatchLogsClient client;

    @BeforeAll
    public static void beforeAll() {
        exportResourcePolicies = new ExportResourcePolicies();
        exportResourcePolicies.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getCloudWatchLogGroupClient();
    }

    private List<AWSResourcePolicy> getResourcePolicies() {
        return List.of(
                AWSResourcePolicy.builder()
                        .resourcePolicy(
                                ResourcePolicy.builder()
                                        .policyName("es_log_resource_policy")
                                        .policyDocument(TestDataFileUtils.asString(
                                                resourceLoader.getResource("testData/aws/input/CloudWatchLogResourcePolicyDocument.json")))
                                        .build()
                        )
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportResourcePolicies.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        List<AWSResourcePolicy> awsResourcePolicies = getResourcePolicies();

        Maps<Resource> resourceMaps = exportResourcePolicies.getResourceMaps(awsResourcePolicies);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/CloudWatchLogResourcePolicy.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/CloudWatchLogResourcePolicy.cmd"));
        String actual = exportResourcePolicies.getTFImport(getResourcePolicies()).script();

        assertEquals(expected, actual);
    }

}