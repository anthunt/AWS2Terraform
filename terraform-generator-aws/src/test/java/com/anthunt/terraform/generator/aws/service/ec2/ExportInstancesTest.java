package com.anthunt.terraform.generator.aws.service.ec2;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.ec2.model.AWSInstance;
import com.anthunt.terraform.generator.aws.service.ec2.model.AWSReservation;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportInstancesTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportInstances exportInstances;

    @BeforeAll
    public static void beforeAll() {
        exportInstances = new ExportInstances();
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();
        Maps<Resource> export = exportInstances.export(ec2Client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getDescribeInstancesResponse() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();

        List<AWSReservation> awsReservations = exportInstances.getReservations(ec2Client);
        log.debug("awsReservations => {}", awsReservations);
    }

    @Test
    public void getResourceMaps() {
        List<AWSReservation> awsReservations = List.of(
                AWSReservation.builder()
                        .instance(AWSInstance.builder()
                                .instance(Instance.builder()
                                        .amiLaunchIndex(0)
                                        .instanceId("i-00025ef3e99e66157")
                                        .imageId("ami-0685efd12a23690f5")
                                        .placement(Placement.builder().groupName("").tenancy("default").build())
                                        .cpuOptions(CpuOptions.builder().coreCount(1).threadsPerCore(2).build())
                                        .ebsOptimized(true)
                                        .instanceType("c5.large")
                                        .keyName("sec-key")
                                        .monitoring(Monitoring.builder().state("false").build())
                                        .securityGroups(List.of(GroupIdentifier.builder().groupId("sg-032bd64bb8901f233").build()))
                                        .subnetId("subnet-45e0000a")
                                        .privateIpAddress("172.31.1.1")
                                        .sourceDestCheck(true)
                                        .tags(List.of(Tag.builder().key("Name").value("windows-desktop").build()))
                                        .hibernationOptions(HibernationOptions.builder().build())
                                        .metadataOptions(InstanceMetadataOptionsResponse.builder()
                                                .httpEndpoint("enabled")
                                                .httpTokens("optional")
                                                .httpPutResponseHopLimit(1).build())
                                        .build()
                                )
                                .userData(null)
                                .disableApiTermination(true)
                                .shutdownBehavior("stop")
                                .build()
                        )
                        .build()
        );

        Maps<Resource> resourceMaps = exportInstances.getResourceMaps(awsReservations);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/ec2/expected/instance.tf")
        );
        assertEquals(expected, actual);
    }

}