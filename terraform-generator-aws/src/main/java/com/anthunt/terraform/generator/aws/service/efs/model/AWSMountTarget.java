package com.anthunt.terraform.generator.aws.service.efs.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.efs.model.MountTargetDescription;

@Data
@Builder
@ToString
public class AWSMountTarget implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_efs_mount_target";

    private MountTargetDescription mountTarget;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return mountTarget.mountTargetId();
    }

    @Override
    public String getResourceName() {
        return mountTarget.mountTargetId();
    }
}
