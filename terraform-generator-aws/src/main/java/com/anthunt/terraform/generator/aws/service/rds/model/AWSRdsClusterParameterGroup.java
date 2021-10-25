package com.anthunt.terraform.generator.aws.service.rds.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.rds.model.DBClusterParameterGroup;
import software.amazon.awssdk.services.rds.model.Parameter;
import software.amazon.awssdk.services.rds.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSRdsClusterParameterGroup implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_rds_cluster_parameter_group";
    private DBClusterParameterGroup dbClusterParameterGroup;
    @Singular
    private List<Parameter> parameters;
    @Singular
    private List<Tag> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return dbClusterParameterGroup.dbClusterParameterGroupName();
    }

    @Override
    public String getResourceName() {
        return dbClusterParameterGroup.dbClusterParameterGroupName();
    }
}
