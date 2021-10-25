package com.anthunt.terraform.generator.aws.service.elb;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.elb.model.AWSTargetGroup;
import com.anthunt.terraform.generator.aws.service.elb.model.AWSTargetGroupAttachment;
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
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportLoadBalancerTargetGroupsTest {

    private static ExportLoadBalancerTargetGroups exportLoadBalancerTargetGroups;
    private static ElasticLoadBalancingV2Client client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportLoadBalancerTargetGroups = new ExportLoadBalancerTargetGroups();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getElasticLoadBalancingV2Client();
    }

    private List<AWSTargetGroup> getAwsTargetGroups() {
        return List.of(
                AWSTargetGroup.builder()
                        .targetGroup(TargetGroup.builder()
                                .targetGroupArn("arn:aws:elasticloadbalancing:us-west-2:187416307283:targetgroup/k8s-ingressn-ingressn-1dab2d3f88/20cfe21448b66314")
                                .targetGroupName("k8s-ingressn-ingressn-1dab2d3f88")
                                .port(30832)
                                .protocol(ProtocolEnum.TCP)
                                .vpcId("vpc-00015ad4b3a1ecefb")
                                .targetType(TargetTypeEnum.INSTANCE)
                                .healthCheckEnabled(true)
                                .healthCheckPort("30035")
                                .healthCheckProtocol(ProtocolEnum.TCP)
                                .healthCheckPath("/healthz")
                                .healthyThresholdCount(2)
                                .unhealthyThresholdCount(2)
                                .healthCheckIntervalSeconds(10)
                                .build())
                        .targetGroupAttribute(TargetGroupAttribute.builder()
                                .key("deregistration_delay.timeout_seconds")
                                .value("300")
                                .build())
                        .awsTargetGroupAttachment(AWSTargetGroupAttachment.builder()
                                .targetGroupName("k8s-ingressn-ingressn-1dab2d3f88")
                                .targetDescription(TargetDescription.builder()
                                        .id("i-00015ef3e99e66157")
                                        .port(30832)
                                        .build())
                                .build())
                        .awsTargetGroupAttachment(AWSTargetGroupAttachment.builder()
                                .targetGroupName("k8s-ingressn-ingressn-1dab2d3f88")
                                .targetDescription(TargetDescription.builder()
                                        .id("i-00025ef3e99e66157")
                                        .port(30832)
                                        .build())
                                .build())
                        .build(),
                AWSTargetGroup.builder()
                        .targetGroup(TargetGroup.builder()
                                .targetGroupArn("arn:aws:elasticloadbalancing:us-west-2:187416307283:targetgroup/tg-dev-service-was/20cfe21448b66314")
                                .targetGroupName("tg-dev-service-was")
                                .port(8080)
                                .protocol(ProtocolEnum.HTTP)
                                .vpcId("vpc-00015ad4b3a1ecefb")
                                .targetType(TargetTypeEnum.IP)
                                .healthCheckEnabled(true)
                                .healthCheckPort("traffic-port")
                                .healthCheckProtocol(ProtocolEnum.HTTP)
                                .healthCheckPath("/health")
                                .healthyThresholdCount(5)
                                .unhealthyThresholdCount(2)
                                .healthCheckIntervalSeconds(30)
                                .build())
                        .targetGroupAttribute(TargetGroupAttribute.builder()
                                .key("deregistration_delay.timeout_seconds")
                                .value("300")
                                .build())
                        .awsTargetGroupAttachment(AWSTargetGroupAttachment.builder()
                                .targetGroupName("tg-dev-service-was")
                                .targetDescription(TargetDescription.builder()
                                        .id("10.100.1.10")
                                        .port(8080)
                                        .build())
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportLoadBalancerTargetGroups.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getTargetGroups() {
        List<AWSTargetGroup> awsTargetGroups = exportLoadBalancerTargetGroups.listAwsTagetGroups(client);
        log.debug("awsTargetGroups => {}", awsTargetGroups);
    }

    @Test
    public void getResourceMaps() {
        List<AWSTargetGroup> awsTargetGroups = getAwsTargetGroups();

        Maps<Resource> resourceMaps = exportLoadBalancerTargetGroups.getResourceMaps(awsTargetGroups);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/LoadBalancerTargetGroup.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/LoadBalancerTargetGroup.cmd"));
        String actual = exportLoadBalancerTargetGroups.getTFImport(getAwsTargetGroups()).script();

        assertEquals(expected, actual);
    }
}