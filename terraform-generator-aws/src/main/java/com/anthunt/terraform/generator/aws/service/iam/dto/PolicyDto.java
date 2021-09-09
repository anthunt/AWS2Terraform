package com.anthunt.terraform.generator.aws.service.iam.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.services.iam.model.Policy;

@Data
@ToString
@Builder
public class PolicyDto {
    private Policy policy;
    private String document;

}
