package com.anthunt.terraform.generator.aws.service.apigateway.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.apigateway.model.Resource;

import java.text.MessageFormat;
import java.util.List;

@Data
@ToString
@Builder
public class AWSResource implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_api_gateway_resource";
    private Resource resource;

    private String restApiName;

    private String restApiId;

    @Singular
    private List<AWSMethod> awsMethods;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return MessageFormat.format("{0}/{1}", restApiId, resource.id());
    }

    @Override
    public String getResourceName() {
        return MessageFormat.format("{0}-{1}", restApiName, resource.id());
    }
}
