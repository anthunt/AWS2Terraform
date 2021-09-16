package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSDBCluster;
import com.anthunt.terraform.generator.aws.support.DisabledOnNoAwsCredentials;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;

import java.util.List;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportRdsClustersTest {

    private static ExportRdsClusters exportRdsClusters;
    private static RdsClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportRdsClusters = new ExportRdsClusters();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getRdsClient();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportRdsClusters.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getTargetGroups() {
        List<AWSDBCluster> awsdbClusters = exportRdsClusters.getDBClusters(client);
        log.debug("awsdbClusters => {}", awsdbClusters);
    }

    @Test
    public void getResourceMaps() {
//        List<AWSTargetGroup> awsTargetGroups = List.of(
//                AWSTargetGroup.builder()
//                        .targetGroup(TargetGroup.builder()
//                                .targetGroupName("k8s-ingressn-ingressn-1dab2d3f88")
//                                .port(30832)
//                                .protocol(ProtocolEnum.TCP)
//                                .vpcId("vpc-00015ad4b3a1ecefb")
//                                .targetType(TargetTypeEnum.INSTANCE)
//                                .healthCheckEnabled(true)
//                                .healthCheckPort("30035")
//                                .healthCheckProtocol(ProtocolEnum.TCP)
//                                .healthCheckPath("/healthz")
//                                .healthyThresholdCount(2)
//                                .unhealthyThresholdCount(2)
//                                .healthCheckIntervalSeconds(10)
//                                .build())
//                        .targetGroupAttribute(TargetGroupAttribute.builder()
//                                .key("deregistration_delay.timeout_seconds")
//                                .value("300")
//                                .build())
//                        .targetDescription(TargetDescription.builder()
//                                .id("i-00015ef3e99e66157")
//                                .port(30832)
//                                .build())
//                        .targetDescription(TargetDescription.builder()
//                                .id("i-00025ef3e99e66157")
//                                .port(30832)
//                                .build())
//                        .build(),
//                AWSTargetGroup.builder()
//                        .targetGroup(TargetGroup.builder()
//                                .targetGroupName("tg-dev-service-was")
//                                .port(8080)
//                                .protocol(ProtocolEnum.HTTP)
//                                .vpcId("vpc-00015ad4b3a1ecefb")
//                                .targetType(TargetTypeEnum.IP)
//                                .healthCheckEnabled(true)
//                                .healthCheckPort("traffic-port")
//                                .healthCheckProtocol(ProtocolEnum.HTTP)
//                                .healthCheckPath("/health")
//                                .healthyThresholdCount(5)
//                                .unhealthyThresholdCount(2)
//                                .healthCheckIntervalSeconds(30)
//                                .build())
//                        .targetGroupAttribute(TargetGroupAttribute.builder()
//                                .key("deregistration_delay.timeout_seconds")
//                                .value("300")
//                                .build())
//                        .targetDescription(TargetDescription.builder()
//                                .id("10.100.1.10")
//                                .port(8080)
//                                .build())
//                        .build()
//        );
//
//        Maps<Resource> resourceMaps = exportRdsClusters.getResourceMaps(awsTargetGroups);
//        String actual = resourceMaps.unmarshall();
//
//        log.debug("actual => \n{}", actual);
//        String expected = TestDataFileUtils.asString(
//                resourceLoader.getResource("testData/elb/expected/LoadBalancerTargetGroup.tf")
//        );
//        assertEquals(expected, actual);

    }
}