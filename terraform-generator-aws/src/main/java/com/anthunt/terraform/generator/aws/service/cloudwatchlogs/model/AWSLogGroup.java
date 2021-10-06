package com.anthunt.terraform.generator.aws.service.cloudwatchlogs.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

import java.util.Map;

@Data
@ToString
@Builder
public class AWSLogGroup {
    private LogGroup logGroup;
    @Singular
    private Map<String,String> tags;
}
