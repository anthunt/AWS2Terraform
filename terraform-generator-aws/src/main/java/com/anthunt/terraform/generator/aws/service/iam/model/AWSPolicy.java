package com.anthunt.terraform.generator.aws.service.iam.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.iam.model.Policy;

@Data
@ToString
@Builder
public class AWSPolicy implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_iam_policy";
    private Policy policy;
    private String document;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return policy.arn();
    }

    @Override
    public String getResourceName() {
        return policy.policyName();
    }
}
