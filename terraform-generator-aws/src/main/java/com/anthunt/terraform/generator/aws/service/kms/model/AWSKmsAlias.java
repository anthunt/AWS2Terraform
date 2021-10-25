package com.anthunt.terraform.generator.aws.service.kms.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.kms.model.AliasListEntry;

@Data
@ToString
@Builder
public class AWSKmsAlias implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_kms_alias";
    private AliasListEntry alias;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return alias.aliasName();
    }

    @Override
    public String getResourceName() {
        String aliasName = alias.aliasName();
        return aliasName.startsWith("alias/") ? aliasName.split("/")[1] : aliasName;
    }
}
