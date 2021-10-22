package com.anthunt.terraform.generator.aws.service.cloudwatchlogs.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

import java.util.Map;

@Data
@ToString
@Builder
public class AWSLogGroup implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_cloudwatch_log_group";

    private LogGroup logGroup;

    @Singular
    private Map<String, String> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return logGroup.logGroupName();
    }

    @Override
    public String getResourceName() {
        String logGroupName = logGroup.logGroupName();
        return logGroupName.startsWith("/") ?
                logGroupName.substring(1).replaceAll("/", "-")
                : logGroupName.replaceAll("/", "-");
    }
}
