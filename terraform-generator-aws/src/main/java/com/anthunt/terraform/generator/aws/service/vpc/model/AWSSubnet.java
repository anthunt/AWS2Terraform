package com.anthunt.terraform.generator.aws.service.vpc.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.Subnet;

@Data
@ToString
@Builder
public class AWSSubnet implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_subnet";
    private Subnet subnet;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return subnet.subnetId();
    }

    @Override
    public String getResourceName() {
        return subnet.subnetId();
    }
}
