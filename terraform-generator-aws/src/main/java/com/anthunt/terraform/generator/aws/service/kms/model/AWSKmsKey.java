package com.anthunt.terraform.generator.aws.service.kms.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.kms.model.AliasListEntry;
import software.amazon.awssdk.services.kms.model.KeyMetadata;

import java.util.List;

@Data
@ToString
@Builder
public class AWSKmsKey {
    private KeyMetadata keyMetadata;
    @Singular
    private List<AWSKmsKeyPolicy> awsKeyPolicies;
    @Singular
    private List<AliasListEntry> aliases;
}
