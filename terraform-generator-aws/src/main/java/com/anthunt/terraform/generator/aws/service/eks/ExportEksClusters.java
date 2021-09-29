package com.anthunt.terraform.generator.aws.service.eks;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.eks.model.AWSEksCluster;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.eks.EksClient;
import software.amazon.awssdk.services.eks.model.Cluster;
import software.amazon.awssdk.services.eks.model.DescribeClusterRequest;
import software.amazon.awssdk.services.eks.model.ListClustersResponse;
import software.amazon.awssdk.services.eks.model.ListTagsForResourceRequest;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportEksClusters extends AbstractExport<EksClient> {

    @Override
    protected Maps<Resource> export(EksClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSEksCluster> awsEksClusters = listAWSEksClusters(client);

        return getResourceMaps(awsEksClusters);

    }

    List<AWSEksCluster> listAWSEksClusters(EksClient client) {
        ListClustersResponse listClustersResponse = client.listClusters();
        return listClustersResponse.clusters().stream()
                .map(clusterName -> {
                    Cluster cluster = client.describeCluster(
                                    DescribeClusterRequest.builder()
                                            .name(clusterName)
                                            .build())
                            .cluster();
                    return AWSEksCluster.builder()
                            .cluster(cluster)
                            .tags(client.listTagsForResource(ListTagsForResourceRequest.builder()
                                            .resourceArn(cluster.arn())
                                            .build())
                                    .tags())
                            .build();
                })
                .collect(Collectors.toList());

    }

    Maps<Resource> getResourceMaps(List<AWSEksCluster> awsEksClusters) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSEksCluster awsEksCluster : awsEksClusters) {
            Cluster cluster = awsEksCluster.getCluster();
            Map<String, String> tags = awsEksCluster.getTags();
            resourceMapsBuilder
                    .map(Resource.builder()
                            .api("aws_eks_cluster")
                            .name(cluster.name())
                            .arguments(
                                    TFArguments.builder()
                                            .argument("name", TFString.build(cluster.name()))
                                            .argument("role_arn", TFString.build(cluster.roleArn()))
                                            .argument("vpc_config", TFMap.builder()
                                                    .map("endpoint_private_access", TFBool.build(cluster.resourcesVpcConfig().endpointPrivateAccess()))
                                                    .map("endpoint_public_access", TFBool.build(cluster.resourcesVpcConfig().endpointPublicAccess()))
                                                    .map("public_access_cidrs", TFList.build(
                                                            cluster.resourcesVpcConfig().publicAccessCidrs().stream()
                                                                    .map(cidr -> TFString.builder().isLineIndent(false).value(cidr).build())
                                                                    .collect(Collectors.toList())))
                                                    .map("security_group_ids", TFList.build(cluster.resourcesVpcConfig().securityGroupIds().stream()
                                                            .map(securityGroupId -> TFExpression.builder().isLineIndent(false).expression(
                                                                            MessageFormat.format("aws_security_group.{0}.id", securityGroupId))
                                                                    .build())
                                                            .collect(Collectors.toList())))
                                                    .map("subnet_ids", TFList.build(cluster.resourcesVpcConfig().subnetIds().stream()
                                                            .map(subnetId -> TFExpression.builder().isLineIndent(false).expression(
                                                                            MessageFormat.format("aws_subnet.{0}.id", subnetId))
                                                                    .build())
                                                            .collect(Collectors.toList())))
                                                    .build())
                                            .argument("kubernetes_network_config", TFMap.builder()
                                                    .map("service_ipv4_cidr", TFString.build(cluster.kubernetesNetworkConfig().serviceIpv4Cidr()))
                                                    .build())
                                            .argument("version", TFString.build(cluster.version()))
                                            .argument("enabled_cluster_log_types", TFList.build(cluster.logging().clusterLogging().stream()
                                                    .findFirst().get().types().stream()
                                                    .map(type -> TFString.builder().isLineIndent(false).value(type.toString())
                                                            .build())
                                                    .collect(Collectors.toList())))
                                            .argument("encryption_config", TFMap.builder()
                                                    .map("provider", TFMap.builder()
                                                            .map("key_arn", TFString.build(cluster.encryptionConfig().stream()
                                                                    .findFirst().get().provider().keyArn()))
                                                            .build())
                                                    .map("resources", TFList.build(cluster.encryptionConfig().stream()
                                                            .findFirst().get().resources().stream()
                                                            .map(resource -> TFString.builder().isLineIndent(false).value(resource)
                                                                    .build())
                                                            .collect(Collectors.toList())))
                                                    .build())
                                            .argument("tags", TFMap.build(
                                                    tags.entrySet().stream()
                                                            .collect(Collectors.toMap(Map.Entry::getKey, tag -> TFString.build(tag.getValue())))
                                            ))
                                            .build())
                            .build())
                    .build();
        }
        return resourceMapsBuilder.build();
    }
}
