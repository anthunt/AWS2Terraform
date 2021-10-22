package com.anthunt.terraform.generator.aws.service.ec2.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.LaunchTemplateVersion;

@Data
@ToString
@Builder
public class AWSLaunchTemplateVersion implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_launch_template";

    private LaunchTemplateVersion launchTemplateVersion;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return launchTemplateVersion.launchTemplateId();
    }

    @Override
    public String getResourceName() {
        return launchTemplateVersion.launchTemplateName();
    }
}
