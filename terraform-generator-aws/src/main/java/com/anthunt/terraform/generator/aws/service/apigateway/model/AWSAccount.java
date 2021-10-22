package com.anthunt.terraform.generator.aws.service.apigateway.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.apigateway.model.GetAccountResponse;

import java.text.MessageFormat;

@Data
@ToString
@Builder
public class AWSAccount implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_api_gateway_account";

    private GetAccountResponse account;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return "api-gateway-account";
    }

    @Override
    public String getResourceName() {
        return MessageFormat.format("{0}-{1}",
                "account",
                account.cloudwatchRoleArn().split(":")[4]);
    }
}
