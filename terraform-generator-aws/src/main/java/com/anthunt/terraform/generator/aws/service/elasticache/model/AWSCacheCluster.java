package com.anthunt.terraform.generator.aws.service.elasticache.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.elasticache.model.CacheCluster;
import software.amazon.awssdk.services.elasticache.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSCacheCluster implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_elasticache_cluster";

    private CacheCluster cacheCluster;
    @Singular
    private List<Tag> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return cacheCluster.cacheClusterId();
    }

    @Override
    public String getResourceName() {
        return cacheCluster.cacheClusterId();
    }
}
