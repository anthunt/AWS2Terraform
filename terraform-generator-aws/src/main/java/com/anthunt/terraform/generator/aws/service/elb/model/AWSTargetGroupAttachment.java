package com.anthunt.terraform.generator.aws.service.elb.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetDescription;

import java.text.MessageFormat;

@Data
@Builder
@ToString
public class AWSTargetGroupAttachment implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_lb_target_group_attachment";
    private TargetDescription targetDescription;
    private String targetGroupName;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return null;
    }

    @Override
    public String getResourceName() {
        return MessageFormat.format("{0}-{1}", targetGroupName, targetDescription.id())
                .replaceAll("\\.", "-");
    }
}
