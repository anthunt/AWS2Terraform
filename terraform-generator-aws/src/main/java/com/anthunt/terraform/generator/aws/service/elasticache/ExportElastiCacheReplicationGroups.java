package com.anthunt.terraform.generator.aws.service.elasticache;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.elasticache.model.AWSCacheReplicationGroup;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
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
public class ExportElastiCacheReplicationGroups extends AbstractExport<ElastiCacheClient> {

    @Override
    protected Maps<Resource> export(ElastiCacheClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSCacheReplicationGroup> awsCacheReplicationGroups = listAwsCacheReplicationGroups(client);
        return getResourceMaps(awsCacheReplicationGroups);
    }

    @Override
    protected TFImport scriptImport(ElastiCacheClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSCacheReplicationGroup> awsCacheReplicationGroups = listAwsCacheReplicationGroups(client);
        return getTFImport(awsCacheReplicationGroups);
    }

    List<AWSCacheReplicationGroup> listAwsCacheReplicationGroups(ElastiCacheClient client) {

        DescribeReplicationGroupsResponse describeReplicationGroups = client.describeReplicationGroups(DescribeReplicationGroupsRequest.builder()
                .build());
        return describeReplicationGroups.replicationGroups().stream()
                .peek(replicationGroup -> log.debug("replicationGroup => {}", replicationGroup))
                .map(replicationGroup -> AWSCacheReplicationGroup.builder()
                        .replicationGroup(replicationGroup)
                        .cacheClusters(replicationGroup.memberClusters().stream()
                                .flatMap(memberCluster -> client.describeCacheClusters(DescribeCacheClustersRequest.builder()
                                        .cacheClusterId(memberCluster)
                                        .build()).cacheClusters().stream())
                                .collect(Collectors.toList())
                        )
                        .tags(client.listTagsForResource(ListTagsForResourceRequest.builder()
                                        .resourceName(replicationGroup.arn())
                                        .build())
                                .tagList())
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSCacheReplicationGroup> awsCacheReplicationGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsCacheReplicationGroups.stream().forEach(awsCacheReplicationGroup -> {
            ReplicationGroup replicationGroup = awsCacheReplicationGroup.getReplicationGroup();
            CacheCluster cacheCluster = awsCacheReplicationGroup.getCacheClusters().stream().findFirst().get();
            List<Tag> tags = awsCacheReplicationGroup.getTags();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_elasticache_replication_group")
                            .name(replicationGroup.replicationGroupId())
                            .argument("replication_group_id", TFString.build(replicationGroup.replicationGroupId()))
                            .argument("replication_group_description", TFString.build(replicationGroup.description()))
                            .argument("node_type", TFString.build(replicationGroup.cacheNodeType()))
                            .argument("engine", TFString.build(cacheCluster.engine()))
                            .argument("engine_version", TFString.build(cacheCluster.engineVersion()))
                            .argument("port", TFNumber.build(replicationGroup.configurationEndpoint().port()))
                            .argument("parameter_group_name", TFString.build(cacheCluster.cacheParameterGroup().cacheParameterGroupName()))
                            .argument("at_rest_encryption_enabled", TFBool.build(replicationGroup.atRestEncryptionEnabled()))
                            .argument("transit_encryption_enabled", TFBool.build(replicationGroup.transitEncryptionEnabled()))
                            .argument("auth_token", TFBool.build(replicationGroup.authTokenEnabled()))
                            .argument("auto_minor_version_upgrade", TFBool.build(cacheCluster.autoMinorVersionUpgrade()))
                            .argument("automatic_failover_enabled", TFString.build(replicationGroup.automaticFailoverAsString()))
                            .argument("number_cache_clusters", TFNumber.build(awsCacheReplicationGroup.getCacheClusters().stream().count()))
                            .argument("snapshot_retention_limit", TFString.build(replicationGroup.snapshotRetentionLimit().toString()))
                            .argument("snapshot_window", TFString.build(replicationGroup.snapshotWindow()))
                            .argument("subnet_group_name", TFString.build(cacheCluster.cacheSubnetGroupName()))
                            .argument("security_group_ids", TFList.build(cacheCluster.securityGroups().stream()
                                    .map(sg -> TFExpression.builder().isLineIndent(false).expression(
                                                    MessageFormat.format("aws_security_group.security_groups.{0}.id", sg.securityGroupId()))
                                            .build())
                                    .collect(Collectors.toList())))
                            .argumentIf(replicationGroup.clusterEnabled(),
                                    "cluster_mode",
                                    TFBlock.builder()
                                            .argument("num_node_groups", TFNumber.build(replicationGroup.nodeGroups().stream().count()))
                                            .argument("replicas_per_node_group", TFNumber.build(replicationGroup.nodeGroups().stream()
                                                    .findFirst().get().nodeGroupMembers().stream().count()))
                                            .build()
                            )
                            .argument("tags", TFMap.build(
                                    tags.stream()
                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                            ))
                            .build());

        });

        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSCacheReplicationGroup> awsCacheReplicationGroups) {
        return TFImport.builder()
                .importLines(awsCacheReplicationGroups.stream()
                        .map(awsCacheReplicationGroup -> TFImportLine.builder()
                                .address(MessageFormat.format("{0}.{1}",
                                        "aws_elasticache_replication_group",
                                        awsCacheReplicationGroup.getReplicationGroup().replicationGroupId()))
                                .id(awsCacheReplicationGroup.getReplicationGroup().replicationGroupId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }

}
