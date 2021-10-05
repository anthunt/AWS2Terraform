package com.anthunt.terraform.generator.aws.service.elasticache;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.elasticache.model.AWSCacheCluster;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.elasticache.ElastiCacheClient;
import software.amazon.awssdk.services.elasticache.model.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportElastiCacheClusters extends AbstractExport<ElastiCacheClient> {

    @Override
    protected Maps<Resource> export(ElastiCacheClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSCacheCluster> awsCacheClusters = listAwsCacheClusters(client);

        return getResourceMaps(awsCacheClusters);

    }

    List<AWSCacheCluster> listAwsCacheClusters(ElastiCacheClient client) {

        DescribeCacheClustersResponse describeCacheClustersResponse = client.describeCacheClusters(DescribeCacheClustersRequest.builder()
                        .showCacheClustersNotInReplicationGroups(true)
                        .showCacheNodeInfo(true)
                .build());
        return describeCacheClustersResponse.cacheClusters().stream()
                .peek(cacheCluster -> log.debug("cacheCluster => {}", cacheCluster))
                .map(cacheCluster -> AWSCacheCluster.builder()
                        .cacheCluster(cacheCluster)
                        .tags(client.listTagsForResource(ListTagsForResourceRequest.builder()
                                        .resourceName(cacheCluster.arn())
                                        .build())
                                .tagList())
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSCacheCluster> awsCacheClusters) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsCacheClusters.stream().forEach(awsCacheCluster -> {
            CacheCluster cacheCluster = awsCacheCluster.getCacheCluster();
            List<Tag> tags = awsCacheCluster.getTags();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_elasticache_cluster")
                            .name(cacheCluster.cacheClusterId())
                            .argument("cluster_id", TFString.build(cacheCluster.cacheClusterId()))
                            .argument("node_type", TFString.build(cacheCluster.cacheNodeType()))
                            .argument("num_cache_nodes", TFNumber.build(cacheCluster.numCacheNodes()))
                            // Optional, Memcached only
//                            .argument("az_mode", TFString.build(""))
                            .argument("engine", TFString.build(cacheCluster.engine()))
                            .argument("engine_version", TFString.build(cacheCluster.engineVersion()))
                            .argument("port", TFNumber.build(cacheCluster.cacheNodes().stream().findFirst().get().endpoint().port()))
                            .argument("parameter_group_name", TFString.build(cacheCluster.cacheParameterGroup().cacheParameterGroupName()))
                            .argument("snapshot_retention_limit", TFString.build(cacheCluster.snapshotRetentionLimit().toString()))
                            .argument("snapshot_window", TFString.build(cacheCluster.snapshotWindow()))
                            .argument("subnet_group_name", TFString.build(cacheCluster.cacheSubnetGroupName()))
                            .argument("security_group_ids", TFList.build(cacheCluster.securityGroups().stream()
                                    .map(sg -> TFExpression.builder().isLineIndent(false).expression(
                                                    MessageFormat.format("aws_security_group.security_groups.{0}.id", sg.securityGroupId()))
                                            .build())
                                    .collect(Collectors.toList())))
                            .argument("tags", TFMap.build(
                                    tags.stream()
                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                            ))
                            .build());

        });

        return resourceMapsBuilder.build();
    }

}
