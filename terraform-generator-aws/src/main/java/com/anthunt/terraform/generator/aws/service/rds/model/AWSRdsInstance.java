package com.anthunt.terraform.generator.aws.service.rds.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.rds.model.DBInstance;

@Data
@Builder
@ToString
public class AWSRdsInstance implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_rds_cluster_instance";
    private DBInstance dbInstance;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return dbInstance.dbInstanceIdentifier();
    }

    @Override
    public String getResourceName() {
        return dbInstance.dbInstanceIdentifier();
    }
//    private List<String> securityGroupNames;
}
