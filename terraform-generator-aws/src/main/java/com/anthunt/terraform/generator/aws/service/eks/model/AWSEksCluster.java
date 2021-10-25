package com.anthunt.terraform.generator.aws.service.eks.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
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
public class AWSEksCluster implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_eks_cluster";

    private Cluster cluster;

    @Singular
    private List<Addon> addons;

    @Singular
    private List<AWSEksNodeGroup> awsEksNodeGroups;

    @Singular
    private Map<String, String> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return cluster.name();
    }

    @Override
    public String getResourceName() {
        return cluster.name();
    }
}
