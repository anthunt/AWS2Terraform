package com.anthunt.terraform.generator.aws.service.eks.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.eks.model.Addon;
import software.amazon.awssdk.services.eks.model.Cluster;

import java.util.List;
import java.util.Map;

@Data
@Builder
@ToString
public class AWSEksCluster {
    private Cluster cluster;

    @Singular
    private List<Addon> addons;

    @Singular
    private List<AWSEksNodeGroup> awsEksNodeGroups;

    @Singular
    private Map<String, String> tags;
}
