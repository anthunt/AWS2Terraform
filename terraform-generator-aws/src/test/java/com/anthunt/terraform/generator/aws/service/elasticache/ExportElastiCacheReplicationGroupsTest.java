package com.anthunt.terraform.generator.aws.service.elasticache;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.elasticache.model.AWSCacheReplicationGroup;
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
class ExportElastiCacheReplicationGroupsTest {

    private static ExportElastiCacheReplicationGroups exportElastiCacheReplicationGroups;
    private static ElastiCacheClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportElastiCacheReplicationGroups = new ExportElastiCacheReplicationGroups();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getElastiCacheClient();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportElastiCacheReplicationGroups.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getCacheReplicationGroups() {
        List<AWSCacheReplicationGroup> awsCacheReplicationGroups = exportElastiCacheReplicationGroups.getReplicationGroupsResponse(client);
        log.debug("awsCacheReplicationGroups => {}", awsCacheReplicationGroups);
    }

    @Test
    public void getResourceMaps() {
        List<AWSCacheReplicationGroup> awsCacheClusters = List.of(
                AWSCacheReplicationGroup.builder()
                        .replicationGroup(ReplicationGroup.builder()
                                .replicationGroupId("redis-session")
                                .description("redis cluster for session cluster")
                                .cacheNodeType("cache.t3.micro")
                                .configurationEndpoint(Endpoint.builder()
                                        .port(16379)
                                        .build())
                                .clusterEnabled(true)
                                .nodeGroups(NodeGroup.builder()
                                        .nodeGroupId("0001")
                                        .nodeGroupMembers(NodeGroupMember.builder().cacheNodeId("0001").build(),
                                                NodeGroupMember.builder().cacheNodeId("0002").build(),
                                                NodeGroupMember.builder().cacheNodeId("0003").build())
                                        .build())
                                .atRestEncryptionEnabled(false)
                                .transitEncryptionEnabled(false)
                                .authTokenEnabled(false)
                                .automaticFailover(AutomaticFailoverStatus.ENABLED)
                                .snapshotRetentionLimit(0)
                                .snapshotWindow("00:00-01:00")
                                .build())
                        .cacheClusters(List.of(
                                        CacheCluster.builder()
                                                .cacheClusterId("redis-dev-cluster-0001-001")
                                                .cacheNodeType("cache.t3.micro")
                                                .numCacheNodes(1)
                                                .engine("redis")
                                                .engineVersion("6.0.5")
                                                .cacheNodes(CacheNode.builder().endpoint(Endpoint.builder()
                                                                .port(6379)
                                                                .build())
                                                        .build())
                                                .cacheParameterGroup(CacheParameterGroupStatus.builder()
                                                        .cacheParameterGroupName("default.redis6.x.cluster.on")
                                                        .build())
                                                .autoMinorVersionUpgrade(true)
                                                .snapshotRetentionLimit(0)
                                                .cacheSubnetGroupName("dev-subnetgroup-session")
                                                .securityGroups(SecurityGroupMembership.builder()
                                                        .securityGroupId("sg-0eac2c2376f703c43")
                                                        .build())
                                                .build(),
                                        CacheCluster.builder()
                                                .cacheClusterId("redis-dev-cluster-0001-002")
                                                .build(),
                                        CacheCluster.builder()
                                                .cacheClusterId("redis-dev-cluster-0001-003")
                                                .build()
                                )
                        )
                        .tag(Tag.builder().key("Name").value("redis-dev").build())
                        .build()
        );

        Maps<Resource> resourceMaps = exportElastiCacheReplicationGroups.getResourceMaps(awsCacheClusters);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/elasticache/expected/ElasticacheReplicationGroup.tf")
        );
        assertEquals(expected, actual);

    }
}