package com.anthunt.terraform.generator.aws.service.rds.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.rds.model.DBClusterParameterGroup;
import software.amazon.awssdk.services.rds.model.Parameter;

import java.util.List;

@Data
@Builder
@ToString
public class AWSDBClusterParameterGroup {
    private DBClusterParameterGroup dbClusterParameterGroup;
    @Singular
    private List<Parameter> parameters;
}
