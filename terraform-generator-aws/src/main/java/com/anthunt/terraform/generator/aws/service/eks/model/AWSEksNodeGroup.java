package com.anthunt.terraform.generator.aws.service.eks.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.eks.model.Nodegroup;

import java.util.Map;

@Data
@Builder
@ToString
public class AWSEksNodeGroup {

    private Nodegroup nodegroup;

    @Singular
    private Map<String, String> tags;
}
