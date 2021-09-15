package com.anthunt.terraform.generator.aws.service.elb.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Listener;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;

@Data
@Builder
@ToString
public class AWSListener {
    private Listener listener;
    private LoadBalancer loadBalancer;
    private TargetGroup targetGroup;
}
