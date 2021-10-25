package com.anthunt.terraform.generator.aws.service.ecr.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ecr.model.Repository;

@Data
@Builder
@ToString
public class AWSRepository implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_ecr_repository";

    private Repository repository;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return repository.repositoryName();
    }

    @Override
    public String getResourceName() {
        return repository.repositoryName().replaceAll("/", "-");
    }
}
