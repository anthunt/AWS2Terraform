package com.anthunt.terraform.generator.aws.service.eks.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.eks.model.Nodegroup;

import java.util.Map;

@Data
@Builder
@ToString
public class AWSEksNodeGroup implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_eks_node_group";

    private Nodegroup nodegroup;

    @Singular
    private Map<String, String> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return null;
    }

    @Override
    public String getResourceName() {
        return null;
    }
}
