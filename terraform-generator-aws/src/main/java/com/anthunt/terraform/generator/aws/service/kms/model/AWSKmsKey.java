package com.anthunt.terraform.generator.aws.service.kms.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.kms.model.KeyMetadata;

import java.text.MessageFormat;
import java.util.List;

@Data
@ToString
@Builder
public class AWSKmsKey implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_kms_key";
    private KeyMetadata keyMetadata;
    @Singular
    private List<AWSKmsKeyPolicy> awsKeyPolicies;
    @Singular
    private List<AWSKmsAlias> awsKmsAliases;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return getKeyMetadata().keyId();
    }

    @Override
    public String getResourceName() {
        return MessageFormat.format("key-{0}", keyMetadata.keyId());
    }
}
