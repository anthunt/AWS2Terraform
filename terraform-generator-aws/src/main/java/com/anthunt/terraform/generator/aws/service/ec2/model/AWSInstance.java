package com.anthunt.terraform.generator.aws.service.ec2.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.Instance;

@Data
@ToString
@Builder
public class AWSInstance implements TerraformSource {

    private static final String TERRAFORM_RESOURCE_NAME = "aws_instance";

    private Instance instance;
    private Boolean disableApiTermination;
    private String shutdownBehavior;
    private String userData;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return instance.instanceId();
    }

    @Override
    public String getResourceName() {
        return instance.instanceId();
    }
}
