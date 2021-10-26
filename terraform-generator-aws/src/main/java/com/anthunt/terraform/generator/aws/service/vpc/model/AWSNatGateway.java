package com.anthunt.terraform.generator.aws.service.vpc.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.NatGateway;

@Data
@ToString
@Builder
public class AWSNatGateway implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_nat_gateway";
    private NatGateway natGateway;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return natGateway.natGatewayId();
    }

    @Override
    public String getResourceName() {
        return natGateway.natGatewayId();
    }
}
