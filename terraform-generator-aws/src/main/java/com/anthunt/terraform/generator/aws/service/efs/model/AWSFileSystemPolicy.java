package com.anthunt.terraform.generator.aws.service.efs.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class AWSFileSystemPolicy implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_efs_file_system_policy";

    private String fileSystemPolicy;

    private String fileSystemId;


    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return fileSystemId;
    }

    @Override
    public String getResourceName() {
        return fileSystemId;
    }
}
