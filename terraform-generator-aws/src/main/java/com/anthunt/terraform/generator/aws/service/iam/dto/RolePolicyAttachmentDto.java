package com.anthunt.terraform.generator.aws.service.iam.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class RolePolicyAttachmentDto {
    String roleName;
    String policyName;
}
