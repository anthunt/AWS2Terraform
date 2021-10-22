package com.anthunt.terraform.generator.aws.service.kms.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class AWSKmsKeyPolicy {
    private String name;
    private String policy;

}
