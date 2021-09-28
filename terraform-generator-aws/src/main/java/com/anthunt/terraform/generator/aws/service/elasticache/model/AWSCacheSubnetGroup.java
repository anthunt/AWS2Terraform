package com.anthunt.terraform.generator.aws.service.elasticache.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.elasticache.model.CacheSubnetGroup;
import software.amazon.awssdk.services.elasticache.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSCacheSubnetGroup {
    private CacheSubnetGroup cacheSubnetGroup;
    @Singular
    private List<Tag> tags;
}
