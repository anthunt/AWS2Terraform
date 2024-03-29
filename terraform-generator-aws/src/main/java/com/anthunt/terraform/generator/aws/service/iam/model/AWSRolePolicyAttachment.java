package com.anthunt.terraform.generator.aws.service.iam.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.text.MessageFormat;

@Data
@ToString
@Builder
public class AWSRolePolicyAttachment implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_iam_role_policy_attachment";
    String roleName;
    String policyName;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return MessageFormat.format("{0}/{1}", roleName, policyName);
    }

    @Override
    public String getResourceName() {
        return MessageFormat.format("{0}-attach-{1}", roleName, policyName);
    }
}
