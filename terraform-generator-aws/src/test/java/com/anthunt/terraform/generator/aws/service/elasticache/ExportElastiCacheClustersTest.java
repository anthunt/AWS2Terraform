package com.anthunt.terraform.generator.aws.service.elasticache;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.elasticache.model.AWSCacheCluster;
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
import software.amazon.awssdk.services.elasticache.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportElastiCacheClustersTest {

    private static ExportElastiCacheClusters exportElastiCacheClusters;
    private static ElastiCacheClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportElastiCacheClusters = new ExportElastiCacheClusters();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getElastiCacheClient();
    }

    private List<AWSCacheCluster> getAwsCacheClusters() {
        List<AWSCacheCluster> awsCacheClusters = List.of(
                AWSCacheCluster.builder()
                        .cacheCluster(CacheCluster.builder()
                                .cacheClusterId("redis-dev-cluster")
                                .cacheNodeType("cache.t3.micro")
                                .numCacheNodes(1)
                                .engine("redis")
                                .engineVersion("6.0.5")
                                .cacheNodes(CacheNode.builder().endpoint(Endpoint.builder()
                                                .port(6379)
                                                .build())
                                        .build())
                                .cacheParameterGroup(CacheParameterGroupStatus.builder()
                                        .cacheParameterGroupName("default.redis6.x")
                                        .build())
                                .snapshotRetentionLimit(0)
                                .snapshotWindow("00:00-01:00")
                                .cacheSubnetGroupName("dev-subnetgroup-session")
                                .securityGroups(SecurityGroupMembership.builder()
                                        .securityGroupId("sg-0eac2c2376f703c43")
                                        .build())
                                .build())
                        .tag(Tag.builder().key("Name").value("redis-dev").build())
                        .build()
        );
        return awsCacheClusters;
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportElastiCacheClusters.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getCacheClusters() {
        List<AWSCacheCluster> awsCacheClusters = exportElastiCacheClusters.listAwsCacheClusters(client);
        log.debug("cacheClusters => {}", awsCacheClusters);
    }

    @Test
    public void getResourceMaps() {
        List<AWSCacheCluster> awsCacheClusters = getAwsCacheClusters();

        Maps<Resource> resourceMaps = exportElastiCacheClusters.getResourceMaps(awsCacheClusters);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/Elasticache.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/Elasticache.cmd"));
        String actual = exportElastiCacheClusters.getTFImport(getAwsCacheClusters()).script();

        assertEquals(expected, actual);
    }
}