package com.anthunt.terraform.generator.aws.service.apigateway.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.apigateway.model.GetIntegrationResponse;
import software.amazon.awssdk.services.apigateway.model.GetMethodResponse;

@Data
@ToString
@Builder
public class AWSMethod {
    private GetMethodResponse method;

    private GetIntegrationResponse integration;
}
