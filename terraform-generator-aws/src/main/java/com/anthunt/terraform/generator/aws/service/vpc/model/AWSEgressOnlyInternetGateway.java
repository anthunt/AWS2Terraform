package com.anthunt.terraform.generator.aws.service.vpc.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;

@Data
@ToString
@Builder
public class AWSEgressOnlyInternetGateway implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_egress_only_internet_gateway";
    private EgressOnlyInternetGateway egressOnlyInternetGateway;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return egressOnlyInternetGateway.egressOnlyInternetGatewayId();
    }

    @Override
    public String getResourceName() {
        return egressOnlyInternetGateway.egressOnlyInternetGatewayId();
    }
}
