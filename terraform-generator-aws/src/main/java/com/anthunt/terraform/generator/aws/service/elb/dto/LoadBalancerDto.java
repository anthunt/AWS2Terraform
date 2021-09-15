package com.anthunt.terraform.generator.aws.service.elb.dto;

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
public class LoadBalancerDto {
    private LoadBalancer loadBalancer;
    @Singular
    private List<LoadBalancerAttribute> loadBalancerAttributes;
    @Singular
    private List<Tag> tags;
}
