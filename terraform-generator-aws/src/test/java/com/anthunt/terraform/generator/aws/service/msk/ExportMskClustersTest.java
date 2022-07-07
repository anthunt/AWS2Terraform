package com.anthunt.terraform.generator.aws.service.msk;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.msk.model.AWSMskCluster;
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
import software.amazon.awssdk.services.kafka.KafkaClient;
import software.amazon.awssdk.services.kafka.model.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportMskClustersTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportMskClusters exportMskClusters;

    private static KafkaClient client;

    @BeforeAll
    public static void beforeAll() {
        exportMskClusters = new ExportMskClusters();
        exportMskClusters.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getKafkaClient();
    }

    private List<AWSMskCluster> getAwsMskClusters() {
        return List.of(
                AWSMskCluster.builder()
                        .clusterInfo(ClusterInfo.builder()
                                .clusterArn("arn:aws:kafka:us-west-2:123456789012:cluster/example/279c0212-d057-4dba-9aa9-1c4e5a25bfc7-3")
                                .clusterName("msk-dev")
                                .currentBrokerSoftwareInfo(BrokerSoftwareInfo.builder()
                                        .kafkaVersion("2.6.1")
                                        .build())
                                .numberOfBrokerNodes(2)
                                .encryptionInfo(EncryptionInfo.builder()
                                        .encryptionAtRest(EncryptionAtRest.builder()
                                                .dataVolumeKMSKeyId("arn:aws:kms:ap-northeast-2:100020003000:key/10002000-227f-4116-a5f1-12ab61377980")
                                                .build())
                                        .encryptionInTransit(EncryptionInTransit.builder()
                                                .inCluster(true)
                                                .build())
                                        .build())
                                .brokerNodeGroupInfo(BrokerNodeGroupInfo.builder()
                                        .clientSubnets("subnet-0f58e2bf1ada4d5c0", "subnet-003e5f077d31b5163")
                                        .storageInfo(StorageInfo.builder()
                                                .ebsStorageInfo(EBSStorageInfo.builder()
                                                        .volumeSize(10)
                                                        .build())
                                                .build())
                                        .instanceType("kafka.t3.small")
                                        .securityGroups("sg-010fc4d6910de29ce")
                                        .build())
                                .tags(Map.of("Name", "msk-dev-ulsp"))
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportMskClusters.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void listClusters() {
        List<AWSMskCluster> awsMskClusters = exportMskClusters.listAwsMskClusters(client);
        log.debug("awsKafkaClusters => {}", awsMskClusters);
    }

    @Test
    public void getResourceMaps() {
        List<AWSMskCluster> awsMskClusters = getAwsMskClusters();

        Maps<Resource> resourceMaps = exportMskClusters.getResourceMaps(awsMskClusters);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/MskCluster.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/MskCluster.cmd"));
        String actual = exportMskClusters.getTFImport(getAwsMskClusters()).script();

        assertEquals(expected, actual);
    }
}