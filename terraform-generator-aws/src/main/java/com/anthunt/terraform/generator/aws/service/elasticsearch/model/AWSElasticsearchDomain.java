package com.anthunt.terraform.generator.aws.service.elasticsearch.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.elasticsearch.model.ElasticsearchDomainStatus;
import software.amazon.awssdk.services.elasticsearch.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSElasticsearchDomain {
    private ElasticsearchDomainStatus elasticsearchDomainStatus;

    @Singular
    private List<Tag> tags;
}
