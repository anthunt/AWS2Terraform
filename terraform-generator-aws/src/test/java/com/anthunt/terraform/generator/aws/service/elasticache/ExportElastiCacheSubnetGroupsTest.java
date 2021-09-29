package com.anthunt.terraform.generator.aws.service.elasticache;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.elasticache.model.AWSCacheSubnetGroup;
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
import software.amazon.awssdk.services.elasticache.ElastiCacheClient;
import software.amazon.awssdk.services.elasticache.model.CacheSubnetGroup;
import software.amazon.awssdk.services.elasticache.model.Subnet;
import software.amazon.awssdk.services.elasticache.model.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportElastiCacheSubnetGroupsTest {

    private static ExportElastiCacheSubnetGroups exportElastiCacheSubnetGroups;
    private static ElastiCacheClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportElastiCacheSubnetGroups = new ExportElastiCacheSubnetGroups();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getElastiCacheClient();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportElastiCacheSubnetGroups.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getCacheSubnetGroups() {
        List<AWSCacheSubnetGroup> awsCacheSubnetGroups = exportElastiCacheSubnetGroups.getCacheSubnetGroups(client);
        log.debug("awsCacheSubnetGroups => {}", awsCacheSubnetGroups);
    }

    @Test
    public void getResourceMaps() {
        List<AWSCacheSubnetGroup> awsRdsSubnetGroup = List.of(
                AWSCacheSubnetGroup.builder()
                        .cacheSubnetGroup(
                                CacheSubnetGroup.builder()
                                        .cacheSubnetGroupName("redis-dev-subnetgrp")
                                        .subnets(Subnet.builder().subnetIdentifier("subnet-000140c12f7a1ca6e").build(),
                                                Subnet.builder().subnetIdentifier("subnet-000240c12f7a1ca6e").build()
                                        )
                                        .build())
                        .tag(Tag.builder().key("Name").value("redis-dev-subnetgrp").build())
                        .build()
        );

        Maps<Resource> resourceMaps = exportElastiCacheSubnetGroups.getResourceMaps(awsRdsSubnetGroup);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/elasticache/expected/ElastiCacheSubnetGroup.tf")
        );
        assertEquals(expected, actual);

    }
}