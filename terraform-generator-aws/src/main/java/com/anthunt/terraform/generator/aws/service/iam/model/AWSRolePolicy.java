package com.anthunt.terraform.generator.aws.service.iam.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.iam.model.GetRolePolicyResponse;

import java.text.MessageFormat;

@Data
@ToString
@Builder
public class AWSRolePolicy implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_iam_role_policy";
    private GetRolePolicyResponse rolePolicy;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return MessageFormat.format("{0}:{1}", rolePolicy.roleName(), rolePolicy.policyName());
    }

    @Override
    public String getResourceName() {
        return rolePolicy.policyName();
    }
}
