package com.anthunt.terraform.generator.aws.service.elasticsearch.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
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
public class AWSElasticsearchDomain implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_elasticsearch_domain";

    private ElasticsearchDomainStatus elasticsearchDomainStatus;

    @Singular
    private List<Tag> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return elasticsearchDomainStatus.domainName();
    }

    @Override
    public String getResourceName() {
        return elasticsearchDomainStatus.domainName();
    }
}
