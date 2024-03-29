package com.anthunt.terraform.generator.aws.service.rds.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.rds.model.DBCluster;

import java.util.List;

@Data
@Builder
@ToString
public class AWSRdsCluster implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_rds_cluster";
    private DBCluster dbCluster;
    @Singular
    private List<AWSRdsInstance> awsRdsInstances;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return dbCluster.dbClusterIdentifier();
    }

    @Override
    public String getResourceName() {
        return dbCluster.dbClusterIdentifier();
    }
//    private List<String> securityGroupNames;
}
