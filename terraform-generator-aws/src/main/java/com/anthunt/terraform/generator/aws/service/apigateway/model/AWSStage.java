package com.anthunt.terraform.generator.aws.service.apigateway.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.apigateway.model.Stage;

@Data
@ToString
@Builder
public class AWSStage implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_api_gateway_stage";
    private Stage stage;
    private AWSDeployment awsDeployment;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return null;
    }

    @Override
    public String getResourceName() {
        return stage.stageName();
    }
}
