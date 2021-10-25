package com.anthunt.terraform.generator.aws.service.elb.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancerAttribute;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSLoadBalancer implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_lb";
    private LoadBalancer loadBalancer;
    @Singular
    private List<LoadBalancerAttribute> loadBalancerAttributes;
    @Singular
    private List<Tag> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return loadBalancer.loadBalancerArn();
    }

    @Override
    public String getResourceName() {
        return loadBalancer.loadBalancerName();
    }
}
