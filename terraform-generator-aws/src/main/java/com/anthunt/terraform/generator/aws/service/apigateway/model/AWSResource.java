package com.anthunt.terraform.generator.aws.service.apigateway.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.apigateway.model.Resource;

import java.util.List;

@Data
@ToString
@Builder
public class AWSResource {
    private Resource resource;

    @Singular
    private List<AWSMethod> awsMethods;
}
