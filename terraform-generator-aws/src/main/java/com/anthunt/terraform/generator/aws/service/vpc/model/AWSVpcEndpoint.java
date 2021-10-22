package com.anthunt.terraform.generator.aws.service.vpc.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;

@Data
@ToString
@Builder
public class AWSVpcEndpoint implements TerraformSource {

    final private static String TERRAFORM_RESOURCE_NAME = "aws_vpc_endpoint";
    private VpcEndpoint vpcEndpoint;

    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceName() {
        return vpcEndpoint.serviceName().replaceAll("\\.", "-");
    }

    @Override
    public String getResourceId() {
        return vpcEndpoint.vpcEndpointId();
    }
}
