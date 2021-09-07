package com.anthunt.terraform.generator.aws.service.ec2.dto;

import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.Instance;

@Data
@ToString
public class InstanceDto {
    private Instance instance;
    private Boolean disableApiTermination;
    private String shutdownBehavior;
    private String userData;
}
