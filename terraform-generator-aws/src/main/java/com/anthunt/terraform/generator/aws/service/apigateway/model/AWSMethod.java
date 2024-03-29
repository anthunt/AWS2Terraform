package com.anthunt.terraform.generator.aws.service.apigateway.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.apigateway.model.GetMethodResponse;

import java.text.MessageFormat;

@Data
@ToString
@Builder
public class AWSMethod implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_api_gateway_method";

    private String restApiId;

    private String restApiName;

    private String resourceId;

    private GetMethodResponse method;

    private AWSIntegration awsIntegration;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return MessageFormat.format("{0}/{1}/{2}", restApiName, resourceId, method.httpMethod());
    }

    @Override
    public String getResourceName() {
        return MessageFormat.format("{0}-{1}-{2}", restApiName, resourceId, method.httpMethod());
    }
}
