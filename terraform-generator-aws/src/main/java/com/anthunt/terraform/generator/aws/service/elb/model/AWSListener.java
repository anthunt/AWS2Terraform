package com.anthunt.terraform.generator.aws.service.elb.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Listener;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;

import java.text.MessageFormat;

@Data
@Builder
@ToString
public class AWSListener implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_lb_listener";
    private Listener listener;
    private LoadBalancer loadBalancer;
    private TargetGroup targetGroup;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return getListener().listenerArn();
    }

    @Override
    public String getResourceName() {
        return MessageFormat.format("{0}-{1}-{2}", loadBalancer.loadBalancerName(), listener.port().toString(), listener.protocolAsString());
    }
}
