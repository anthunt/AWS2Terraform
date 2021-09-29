package com.anthunt.terraform.generator.aws.service.eks.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.eks.model.Cluster;

import java.util.Map;

@Data
@Builder
@ToString
public class AWSEksCluster {
    private Cluster cluster;
    @Singular
    private Map<String, String> tags;
}
