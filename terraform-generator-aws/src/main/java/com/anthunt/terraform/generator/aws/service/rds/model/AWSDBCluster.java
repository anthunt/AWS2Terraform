package com.anthunt.terraform.generator.aws.service.rds.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.rds.model.DBCluster;

import java.util.List;

@Data
@Builder
@ToString
public class AWSDBCluster {
    private DBCluster dbCluster;
    private List<String> securityGroupNames;
}
