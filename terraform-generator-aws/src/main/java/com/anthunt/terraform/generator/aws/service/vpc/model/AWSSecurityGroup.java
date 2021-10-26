package com.anthunt.terraform.generator.aws.service.vpc.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;

@Data
@ToString
@Builder
public class AWSSecurityGroup implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_security_group";
    private SecurityGroup securityGroup;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return securityGroup.groupId();
    }

    @Override
    public String getResourceName() {
        return securityGroup.groupName();
    }
}
