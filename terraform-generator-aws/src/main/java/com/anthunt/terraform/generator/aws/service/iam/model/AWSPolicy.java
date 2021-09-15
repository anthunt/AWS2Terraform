package com.anthunt.terraform.generator.aws.service.iam.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.iam.model.Policy;

@Data
@ToString
@Builder
public class AWSPolicy {
    private Policy policy;
    private String document;

}
