package com.anthunt.terraform.generator.aws.service.elasticache.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.elasticache.model.CacheCluster;
import software.amazon.awssdk.services.elasticache.model.ReplicationGroup;
import software.amazon.awssdk.services.elasticache.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSCacheReplicationGroup implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_elasticache_replication_group";

    private ReplicationGroup replicationGroup;
    @Singular
    private List<CacheCluster> cacheClusters;
    @Singular
    private List<Tag> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return replicationGroup.replicationGroupId();
    }

    @Override
    public String getResourceName() {
        return replicationGroup.replicationGroupId();
    }
}
