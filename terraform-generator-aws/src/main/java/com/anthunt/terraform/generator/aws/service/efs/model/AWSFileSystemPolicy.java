package com.anthunt.terraform.generator.aws.service.efs.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.efs.model.FileSystemDescription;
import software.amazon.awssdk.services.efs.model.Tag;

@Data
@Builder
@ToString
public class AWSFileSystemPolicy implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_efs_file_system_policy";

    private String fileSystemPolicy;

    private FileSystemDescription fileSystemDescription;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return fileSystemDescription.fileSystemId();
    }

    @Override
    public String getResourceName() {
        return fileSystemDescription.tags().stream()
                .filter(tag -> tag.key().equals("Name"))
                .findFirst()
                .map(Tag::value)
                .orElse(fileSystemDescription.fileSystemId());

    }
}
