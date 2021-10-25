package com.anthunt.terraform.generator.aws.service.iam.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.iam.model.Role;

@Data
@ToString
@Builder
public class AWSRole implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_iam_role";
    private Role role;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return role.roleName();
    }

    @Override
    public String getResourceName() {
        return role.roleName();
    }
}
