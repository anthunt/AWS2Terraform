package com.anthunt.terraform.generator.aws.service.apigateway.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.apigateway.model.GetDeploymentResponse;
import software.amazon.awssdk.services.apigateway.model.Stage;

@Data
@ToString
@Builder
public class AWSStage {

    private Stage stage;
    private GetDeploymentResponse deployment;

}
