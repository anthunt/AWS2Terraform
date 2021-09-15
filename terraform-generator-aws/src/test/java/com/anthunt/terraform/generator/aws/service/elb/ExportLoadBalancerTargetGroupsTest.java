package com.anthunt.terraform.generator.aws.service.elb;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.elb.model.TargetGroupDto;
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

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportLoadBalancerTargetGroups.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getTargetGroups() {
        List<TargetGroupDto> loadBalancerDtos = exportLoadBalancerTargetGroups.getTagetGroups(client);
        log.debug("loadBalancerDtos => {}", loadBalancerDtos);
    }

    @Test
    public void getResourceMaps() {
        List<TargetGroupDto> targetGroupDto = List.of(
                TargetGroupDto.builder()
                        .targetGroup(TargetGroup.builder()
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
                        .build(),
                TargetGroupDto.builder()
                        .targetGroup(TargetGroup.builder()
                                .targetGroupName("tg-dev-service-was")
                                .port(8080)
                                .protocol(ProtocolEnum.HTTP)
                                .vpcId("vpc-00015ad4b3a1ecefb")
                                .targetType(TargetTypeEnum.INSTANCE)
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
                        .build()
        );

        Maps<Resource> resourceMaps = exportLoadBalancerTargetGroups.getResourceMaps(targetGroupDto);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/elb/expected/LoadBalancerTargetGroup.tf")
        );
        assertEquals(expected, actual);

    }
}