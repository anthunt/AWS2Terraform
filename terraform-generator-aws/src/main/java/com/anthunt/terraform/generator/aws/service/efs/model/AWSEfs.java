package com.anthunt.terraform.generator.aws.service.efs.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.efs.model.FileSystemDescription;
import software.amazon.awssdk.services.efs.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSEfs implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_efs_file_system";

    private FileSystemDescription fileSystemDescription;
    private AWSBackupPolicy awsBackupPolicy;
    private AWSFileSystemPolicy awsFileSystemPolicy;
    private List<AWSMountTarget> awsMountTargets;

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
                .map(Tag::value).orElse(fileSystemDescription.fileSystemId());
    }
}
