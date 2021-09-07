package com.anthunt.terraform.generator.aws.service.vpc.dto;

import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;

@Data
@ToString
public class SecurityGroupDto {
    private SecurityGroup securityGroup;
    private String vpcName;
}
