package com.anthunt.terraform.generator.aws.service.elb;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.elb.model.AWSListener;
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
class ExportLoadBalancerListenersTest {

    private static ExportLoadBalancerListeners exportLoadBalancerListeners;
    private static ElasticLoadBalancingV2Client client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportLoadBalancerListeners = new ExportLoadBalancerListeners();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getElasticLoadBalancingV2Client();
    }

    private List<AWSListener> getAwsListeners() {
        return List.of(
                AWSListener.builder()
                        .listener(Listener.builder()
                                .listenerArn("arn:aws:elasticloadbalancing:us-west-2:187416307283:listener/app/front-end-alb/8e4497da625e2d8a/9ab28ade35828f96")
                                .port(80)
                                .protocol(ProtocolEnum.TCP)
                                .defaultActions(Action.builder()
                                        .targetGroupArn("xxxxxxxxxx")
                                        .type(ActionTypeEnum.FORWARD)
                                        .build())
                                .build())
                        .loadBalancer(LoadBalancer.builder()
                                .loadBalancerName("0111567db2d1f4d02b7493427dc824d8")
                                .build())
                        .targetGroup(TargetGroup.builder()
                                .targetGroupName("k8s-ingressn-ingressn-1dab2d3f88")
                                .targetGroupArn("xxxxxxxxxx")
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportLoadBalancerListeners.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getListeners() {
        List<AWSListener> awsListeners = exportLoadBalancerListeners.listAwsListeners(client);
        log.debug("awsListeners => {}", awsListeners);
    }

    @Test
    public void getResourceMaps() {
        List<AWSListener> awsListeners = getAwsListeners();

        Maps<Resource> resourceMaps = exportLoadBalancerListeners.getResourceMaps(awsListeners);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/LoadBalancerListener.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/LoadBalancerListener.cmd"));
        String actual = exportLoadBalancerListeners.getTFImport(getAwsListeners()).script();

        assertEquals(expected, actual);
    }
}