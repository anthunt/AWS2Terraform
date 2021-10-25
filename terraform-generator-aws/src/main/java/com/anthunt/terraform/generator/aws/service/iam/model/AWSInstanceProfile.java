package com.anthunt.terraform.generator.aws.service.iam.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.iam.model.InstanceProfile;

@Data
@ToString
@Builder
public class AWSInstanceProfile implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_iam_instance_profile";
    private InstanceProfile instanceProfile;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return instanceProfile.instanceProfileName();
    }

    @Override
    public String getResourceName() {
        return instanceProfile.instanceProfileName();
    }
}
