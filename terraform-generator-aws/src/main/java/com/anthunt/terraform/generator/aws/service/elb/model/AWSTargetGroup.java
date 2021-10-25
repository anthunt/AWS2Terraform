package com.anthunt.terraform.generator.aws.service.elb.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Tag;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroupAttribute;

import java.util.List;

@Data
@Builder
@ToString
public class AWSTargetGroup implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_lb_target_group";
    private TargetGroup targetGroup;
    @Singular
    private List<TargetGroupAttribute> targetGroupAttributes;
    @Singular
    private List<AWSTargetGroupAttachment> awsTargetGroupAttachments;
    @Singular
    private List<Tag> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return getTargetGroup().targetGroupArn();
    }

    @Override
    public String getResourceName() {
        return targetGroup.targetGroupName();
    }
}
