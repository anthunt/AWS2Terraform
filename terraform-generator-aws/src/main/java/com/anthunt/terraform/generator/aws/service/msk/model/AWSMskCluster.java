package com.anthunt.terraform.generator.aws.service.msk.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.kafka.model.ClusterInfo;

@Data
@Builder
@ToString
public class AWSMskCluster {
    private ClusterInfo clusterInfo;
}
