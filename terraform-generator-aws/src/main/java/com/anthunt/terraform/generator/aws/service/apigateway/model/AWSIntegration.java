package com.anthunt.terraform.generator.aws.service.apigateway.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.apigateway.model.GetIntegrationResponse;

import java.text.MessageFormat;

@Data
@ToString
@Builder
public class AWSIntegration implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_api_gateway_integration";

    private String restApiName;
    private String restApiId;
    private String resourceId;
    private GetIntegrationResponse integration;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return MessageFormat.format("{0}/{1}/{2}", restApiName, resourceId, integration.httpMethod());
    }

    @Override
    public String getResourceName() {
        return MessageFormat.format("{0}-{1}-{2}", restApiName, resourceId, integration.httpMethod());
    }
}
