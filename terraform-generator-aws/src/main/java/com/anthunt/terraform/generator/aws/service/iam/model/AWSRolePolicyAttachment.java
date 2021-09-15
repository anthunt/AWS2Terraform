package com.anthunt.terraform.generator.aws.service.iam.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class AWSRolePolicyAttachment {
    String roleName;
    String policyName;
}
