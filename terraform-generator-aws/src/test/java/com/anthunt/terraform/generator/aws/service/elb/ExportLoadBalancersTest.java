package com.anthunt.terraform.generator.aws.service.elb;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.elb.model.AWSLoadBalancer;
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
class ExportLoadBalancersTest {

    private static ExportLoadBalancers exportLoadBalancers;
    private static ElasticLoadBalancingV2Client client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportLoadBalancers = new ExportLoadBalancers();
        exportLoadBalancers.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getElasticLoadBalancingV2Client();
    }

    private List<AWSLoadBalancer> getAwsLoadBalancers() {
        return List.of(
                AWSLoadBalancer.builder().loadBalancer(LoadBalancer.builder()
                                .loadBalancerArn("arn:aws:elasticloadbalancing:us-west-2:123456789012:loadbalancer/app/my-load-balancer/50dc6c495c0c9188")
                                .loadBalancerName("a000567db2d1f4d02b7493427dc88888")
                                .scheme(LoadBalancerSchemeEnum.INTERNET_FACING)
                                .type(LoadBalancerTypeEnum.NETWORK)
                                .availabilityZones(AvailabilityZone.builder().subnetId("subnet-05837fce269e60001").build(),
                                        AvailabilityZone.builder().subnetId("subnet-05837fce269e60002").build())
                                .ipAddressType(IpAddressType.IPV4)
                                .build())
                        .loadBalancerAttribute(LoadBalancerAttribute.builder()
                                .key("deletion_protection.enabled")
                                .value("false")
                                .build())
                        .loadBalancerAttribute(LoadBalancerAttribute.builder()
                                .key("load_balancing.cross_zone.enabled")
                                .value("true")
                                .build())
                        .tag(Tag.builder().key("kubernetes.io/service-name").value("ingress-nginx/ingress-nginx-controller")
                                .build())
                        .tag(Tag.builder().key("kubernetes.io/cluster/eks-dev-app-cluster").value("owned")
                                .build())
                        .build(),
                AWSLoadBalancer.builder().loadBalancer(LoadBalancer.builder()
                                .loadBalancerName("b000567db2d1f4d02b7493427dc88888")
                                .scheme(LoadBalancerSchemeEnum.INTERNET_FACING)
                                .type(LoadBalancerTypeEnum.APPLICATION)
                                .availabilityZones(AvailabilityZone.builder().subnetId("subnet-05837fce269e60003").build(),
                                        AvailabilityZone.builder().subnetId("subnet-05837fce269e60004").build())
                                .ipAddressType(IpAddressType.IPV4)
                                .build())
                        .loadBalancerAttribute(LoadBalancerAttribute.builder()
                                .key("deletion_protection.enabled")
                                .value("false")
                                .build())
                        .loadBalancerAttribute(LoadBalancerAttribute.builder()
                                .key("load_balancing.cross_zone.enabled")
                                .value("false")
                                .build())
                        .build(),
                AWSLoadBalancer.builder().loadBalancer(LoadBalancer.builder()
                                .loadBalancerName("c000567db2d1f4d02b7493427dc88888")
                                .scheme(LoadBalancerSchemeEnum.INTERNET_FACING)
                                .type(LoadBalancerTypeEnum.APPLICATION)
                                .availabilityZones(AvailabilityZone.builder().subnetId("subnet-05837fce269e60003")
                                                .loadBalancerAddresses(LoadBalancerAddress.builder()
                                                        .allocationId("allication-01")
                                                        .build())
                                                .build(),
                                        AvailabilityZone.builder().subnetId("subnet-05837fce269e60004")
                                                .loadBalancerAddresses(LoadBalancerAddress.builder()
                                                        .allocationId("allication-02")
                                                        .build())
                                                .build())
                                .ipAddressType(IpAddressType.IPV4)
                                .build())
                        .loadBalancerAttribute(LoadBalancerAttribute.builder()
                                .key("deletion_protection.enabled")
                                .value("false")
                                .build())
                        .loadBalancerAttribute(LoadBalancerAttribute.builder()
                                .key("load_balancing.cross_zone.enabled")
                                .value("false")
                                .build())
                        .build(),
                AWSLoadBalancer.builder().loadBalancer(LoadBalancer.builder()
                                .loadBalancerName("d000567db2d1f4d02b7493427dc88888")
                                .scheme(LoadBalancerSchemeEnum.INTERNET_FACING)
                                .type(LoadBalancerTypeEnum.APPLICATION)
                                .availabilityZones(AvailabilityZone.builder().subnetId("subnet-05837fce269e60003")
                                                .loadBalancerAddresses(LoadBalancerAddress.builder()
                                                        .privateIPv4Address("10.0.1.15")
                                                        .build())
                                                .build(),
                                        AvailabilityZone.builder().subnetId("subnet-05837fce269e60004")
                                                .loadBalancerAddresses(LoadBalancerAddress.builder()
                                                        .privateIPv4Address("10.0.2.15")
                                                        .build())
                                                .build())
                                .ipAddressType(IpAddressType.IPV4)
                                .build())
                        .loadBalancerAttribute(LoadBalancerAttribute.builder()
                                .key("deletion_protection.enabled")
                                .value("false")
                                .build())
                        .loadBalancerAttribute(LoadBalancerAttribute.builder()
                                .key("load_balancing.cross_zone.enabled")
                                .value("false")
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportLoadBalancers.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getLoadBalancers() {
        List<AWSLoadBalancer> loadBalancers = exportLoadBalancers.listAwsLoadBalancers(client);
        log.debug("loadBalancers => {}", loadBalancers);
    }

    @Test
    public void getResourceMaps() {
        List<AWSLoadBalancer> AWSLoadBalancers = getAwsLoadBalancers();

        Maps<Resource> resourceMaps = exportLoadBalancers.getResourceMaps(AWSLoadBalancers);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/LoadBalancer.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/LoadBalancer.cmd"));
        String actual = exportLoadBalancers.getTFImport(getAwsLoadBalancers()).script();

        assertEquals(expected, actual);
    }
}