package com.anthunt.terraform.generator.aws.service.elb;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.elb.dto.LoadBalancerDto;
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
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getElasticLoadBalancingV2Client();
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
        List<LoadBalancerDto> loadBalancers = exportLoadBalancers.getLoadBalancers(client);
        log.debug("polices => {}", loadBalancers);
    }

    @Test
    public void getResourceMaps() {
        List<LoadBalancerDto> loadBalancerDtos = List.of(
                LoadBalancerDto.builder().loadBalancer(LoadBalancer.builder()
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
                LoadBalancerDto.builder().loadBalancer(LoadBalancer.builder()
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
                LoadBalancerDto.builder().loadBalancer(LoadBalancer.builder()
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
                LoadBalancerDto.builder().loadBalancer(LoadBalancer.builder()
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

        Maps<Resource> resourceMaps = exportLoadBalancers.getResourceMaps(loadBalancerDtos);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/elb/expected/LoadBalancer.tf")
        );
        assertEquals(expected, actual);
    }
}