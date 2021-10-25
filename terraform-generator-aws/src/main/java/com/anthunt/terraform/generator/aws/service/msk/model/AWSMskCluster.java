package com.anthunt.terraform.generator.aws.service.msk.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.kafka.model.ClusterInfo;

@Data
@Builder
@ToString
public class AWSMskCluster implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_msk_cluster";
    private ClusterInfo clusterInfo;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return clusterInfo.clusterArn();
    }

    @Override
    public String getResourceName() {
        return clusterInfo.clusterName();
    }
}
