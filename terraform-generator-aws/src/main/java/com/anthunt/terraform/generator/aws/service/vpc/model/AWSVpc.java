package com.anthunt.terraform.generator.aws.service.vpc.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.Vpc;

@Data
@ToString
@Builder
public class AWSVpc {
    private Vpc vpc;
    private boolean enableDnsSupport;
    private boolean enableDnsHostnames;
}
