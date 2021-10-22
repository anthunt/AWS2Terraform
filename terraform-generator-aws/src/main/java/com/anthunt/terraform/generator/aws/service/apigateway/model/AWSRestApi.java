package com.anthunt.terraform.generator.aws.service.apigateway.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.apigateway.model.RestApi;

import java.util.List;

@Data
@ToString
@Builder
public class AWSRestApi implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_api_gateway_rest_api";
    private RestApi restApi;

    @Singular
    private List<AWSStage> awsStages;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return restApi.id();
    }

    @Override
    public String getResourceName() {
        return restApi.name();
    }
}
