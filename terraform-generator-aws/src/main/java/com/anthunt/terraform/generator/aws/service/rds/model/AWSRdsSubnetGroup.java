package com.anthunt.terraform.generator.aws.service.rds.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.rds.model.DBSubnetGroup;
import software.amazon.awssdk.services.rds.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSRdsSubnetGroup implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_db_subnet_group";
    private DBSubnetGroup dbSubnetGroup;
    @Singular
    private List<Tag> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return dbSubnetGroup.dbSubnetGroupName();
    }

    @Override
    public String getResourceName() {
        return dbSubnetGroup.dbSubnetGroupName();
    }
}
