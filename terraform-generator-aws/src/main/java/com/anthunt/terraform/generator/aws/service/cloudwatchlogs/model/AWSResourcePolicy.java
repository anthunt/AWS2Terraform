package com.anthunt.terraform.generator.aws.service.cloudwatchlogs.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.cloudwatchlogs.model.ResourcePolicy;

@Data
@ToString
@Builder
public class AWSResourcePolicy implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_cloudwatch_log_resource_policy";

    private ResourcePolicy resourcePolicy;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return resourcePolicy.policyName();
    }

    @Override
    public String getResourceName() {
        return resourcePolicy.policyName();
    }
}
