package com.anthunt.terraform.generator.aws.service.rds.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rds.model.DBInstance;

import java.util.List;

@Data
@Builder
@ToString
public class AWSRdsCluster {
    private DBCluster dbCluster;
    private List<DBInstance> dbClusterInstances;
//    private List<String> securityGroupNames;
}
