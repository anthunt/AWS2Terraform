package com.anthunt.terraform.generator.aws.service.vpc.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.InternetGateway;

@Data
@ToString
@Builder
public class AWSInternetGateway implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_internet_gateway";
    private InternetGateway internetGateway;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return internetGateway.internetGatewayId();
    }

    @Override
    public String getResourceName() {
        return internetGateway.internetGatewayId();
    }
}
