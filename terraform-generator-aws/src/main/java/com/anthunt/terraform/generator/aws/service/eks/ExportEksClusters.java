package com.anthunt.terraform.generator.aws.service.eks;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.eks.model.AWSEksCluster;
import com.anthunt.terraform.generator.aws.service.eks.model.AWSEksNodeGroup;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.eks.EksClient;
import software.amazon.awssdk.services.eks.model.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportEksClusters extends AbstractExport<EksClient> {

    @Override
    protected Maps<Resource> export(EksClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSEksCluster> awsEksClusters = listAWSEksClusters(client);
        return getResourceMaps(awsEksClusters);
    }

    @Override
    protected TFImport scriptImport(EksClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSEksCluster> awsEksClusters = listAWSEksClusters(client);
        return getTFImport(awsEksClusters);
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
                            .addons(client.listAddons(ListAddonsRequest.builder()
                                            .clusterName(clusterName)
                                            .build())
                                    .addons().stream().map(addon -> client.describeAddon(DescribeAddonRequest.builder()
                                                    .clusterName(clusterName)
                                                    .addonName(addon)
                                                    .build())
                                            .addon())
                                    .collect(Collectors.toList())
                            )
                            .awsEksNodeGroups(client.listNodegroups(ListNodegroupsRequest.builder()
                                            .clusterName(clusterName)
                                            .build())
                                    .nodegroups().stream()
                                    .peek(nodeGroupName -> log.debug("nodeGroupName => {}", nodeGroupName))
                                    .map(nodeGroupName -> AWSEksNodeGroup.builder().nodegroup(
                                                    client.describeNodegroup(DescribeNodegroupRequest.builder()
                                                                    .clusterName(clusterName)
                                                                    .nodegroupName(nodeGroupName)
                                                                    .build())
                                                            .nodegroup())
                                            .build())
                                    .peek(nodeGroup -> log.debug("nodeGroup => {}", nodeGroup))
                                    .collect(Collectors.toList())
                            )
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
                            .argument("name", TFString.build(cluster.name()))
                            .argument("role_arn", TFString.build(cluster.roleArn()))
                            .argument("vpc_config", TFBlock.builder()
                                    .argument("endpoint_private_access", TFBool.build(cluster.resourcesVpcConfig().endpointPrivateAccess()))
                                    .argument("endpoint_public_access", TFBool.build(cluster.resourcesVpcConfig().endpointPublicAccess()))
                                    .argument("public_access_cidrs", TFList.build(
                                            cluster.resourcesVpcConfig().publicAccessCidrs().stream()
                                                    .map(cidr -> TFString.builder().isLineIndent(false).value(cidr).build())
                                                    .collect(Collectors.toList())))
                                    .argument("security_group_ids", TFList.build(cluster.resourcesVpcConfig().securityGroupIds().stream()
                                            .map(securityGroupId -> TFExpression.builder().isLineIndent(false).expression(
                                                            MessageFormat.format("aws_security_group.{0}.id", securityGroupId))
                                                    .build())
                                            .collect(Collectors.toList())))
                                    .argument("subnet_ids", TFList.build(cluster.resourcesVpcConfig().subnetIds().stream()
                                            .map(subnetId -> TFExpression.builder().isLineIndent(false).expression(
                                                            MessageFormat.format("aws_subnet.{0}.id", subnetId))
                                                    .build())
                                            .collect(Collectors.toList())))
                                    .build())
                            .argument("kubernetes_network_config", TFBlock.builder()
                                    .argument("service_ipv4_cidr", TFString.build(cluster.kubernetesNetworkConfig().serviceIpv4Cidr()))
                                    .build())
                            .argument("version", TFString.build(cluster.version()))
                            .argument("enabled_cluster_log_types", TFList.build(cluster.logging().clusterLogging().stream()
                                    .findFirst().get().types().stream()
                                    .map(type -> TFString.builder().isLineIndent(false).value(type.toString())
                                            .build())
                                    .collect(Collectors.toList())))
                            .argument("encryption_config", TFBlock.builder()
                                    .argument("provider", TFBlock.builder()
                                            .argument("key_arn", TFString.build(cluster.encryptionConfig().stream()
                                                    .findFirst().get().provider().keyArn()))
                                            .build())
                                    .argument("resources", TFList.build(cluster.encryptionConfig().stream()
                                            .findFirst().get().resources().stream()
                                            .map(resource -> TFString.builder().isLineIndent(false).value(resource)
                                                    .build())
                                            .collect(Collectors.toList())))
                                    .build())
                            .argument("tags", TFMap.build(
                                    tags.entrySet().stream()
                                            .collect(Collectors.toMap(Map.Entry::getKey, tag -> TFString.build(tag.getValue())))
                            ))
                            .build());
            awsEksCluster.getAddons().forEach(addon ->
                    resourceMapsBuilder
                            .map(Resource.builder()
                                    .api("aws_eks_addon")
                                    .name(MessageFormat.format("{0}-{1}", addon.clusterName(), addon.addonName()))
                                    .argument("cluster_name", TFString.build(addon.clusterName()))
                                    .argument("addon_name", TFString.build(addon.addonName()))
                                    .argument("addon_version", TFString.build(addon.addonVersion()))
                                    .build())
                            .build()
            );
            awsEksCluster.getAwsEksNodeGroups().forEach(awsEksNodegroup -> {
                        Nodegroup nodegroup = awsEksNodegroup.getNodegroup();
                        Map<String, String> nodegroupTags = awsEksNodegroup.getTags();
                        log.debug("nodegroup.diskSize => {}", nodegroup.diskSize());
                        resourceMapsBuilder
                                .map(Resource.builder()
                                        .api("aws_eks_node_group")
                                        .name(nodegroup.nodegroupName())

                                        .argument("cluster_name", TFString.build(nodegroup.clusterName()))
                                        .argument("node_group_name", TFString.build(nodegroup.nodegroupName()))
                                        .argument("node_role_arn", TFString.build(nodegroup.nodeRole()))
                                        .argument("subnet_ids", TFList.build(nodegroup.subnets().stream()
                                                .map(subnetId -> TFExpression.builder().isLineIndent(false).expression(
                                                                MessageFormat.format("aws_subnet.{0}.id", subnetId))
                                                        .build())
                                                .collect(Collectors.toList())))
                                        .argument("ami_type", TFString.build(nodegroup.amiTypeAsString()))
                                        .argument("capacity_type", TFString.build(nodegroup.capacityTypeAsString()))
                                        .argument("disk_size", TFNumber.build(Optional.ofNullable(nodegroup.diskSize())
                                                .map(diskSize -> diskSize.toString()).orElse(null)))
                                        .argument("instance_types", TFList.build(nodegroup.instanceTypes().stream()
                                                .map(instanceType -> TFString.builder().isLineIndent(false).value(instanceType)
                                                        .build())
                                                .collect(Collectors.toList())))
                                        .argument("labels", TFMap.build(
                                                nodegroup.labels().entrySet().stream()
                                                        .collect(Collectors.toMap(Map.Entry::getKey, tag -> TFString.build(tag.getValue())))
                                        ))
                                        .argument("release_version", TFString.build(nodegroup.releaseVersion()))
                                        .argumentIf(Optional.ofNullable(nodegroup.launchTemplate()).isPresent(),
                                                "launch_template",
                                                () -> TFMap.builder()
                                                        .map("name", TFString.build(nodegroup.launchTemplate().name()))
                                                        .map("version", TFString.build(nodegroup.launchTemplate().version()))
                                                        .build())

                                        .argumentIf(Optional.ofNullable(nodegroup.remoteAccess()).isPresent(),
                                                "remote_access",
                                                () -> TFMap.builder()
                                                        .map("ec2_ssh_key", TFString.build(nodegroup.remoteAccess().ec2SshKey()))
                                                        .map("source_security_group_ids", TFList.build(
                                                                nodegroup.remoteAccess().sourceSecurityGroups().stream()
                                                                        .map(sourceSecurityGroup -> TFString.builder().isLineIndent(false)
                                                                                .value(sourceSecurityGroup)
                                                                                .build())
                                                                        .collect(Collectors.toList()))
                                                        )
                                                        .build()
                                        )
                                        .argumentsIf(Optional.ofNullable(nodegroup.taints()).isPresent(),
                                                "taint",
                                                () -> nodegroup.taints().stream()
                                                        .map(taint -> TFMap.builder()
                                                                .map("key", TFString.build(taint.key()))
                                                                .map("value", TFString.build(taint.value()))
                                                                .map("effect", TFString.build(taint.effectAsString()))
                                                                .build())
                                                        .collect(Collectors.toList()))
                                        .argumentIf(Optional.ofNullable(nodegroup.scalingConfig()).isPresent(),
                                                "scaling_config",
                                                () -> TFBlock.builder()
                                                        .argument("desired_size", TFNumber.build(nodegroup.scalingConfig().desiredSize()))
                                                        .argument("max_size", TFNumber.build(nodegroup.scalingConfig().maxSize()))
                                                        .argument("min_size", TFNumber.build(nodegroup.scalingConfig().minSize()))
                                                        .build())
                                        .argument("tags", TFMap.build(
                                                nodegroupTags.entrySet().stream()
                                                        .collect(Collectors.toMap(Map.Entry::getKey, tag -> TFString.build(tag.getValue())))
                                        ))
                                        .build()
                                );
                    }
            );


        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSEksCluster> awsEksClusters) {
        return TFImport.builder()
                .importLines(awsEksClusters.stream()
                        .map(awsEksCluster -> TFImportLine.builder()
                                .address(MessageFormat.format("{0}.{1}",
                                        "aws_eks_cluster",
                                        awsEksCluster.getCluster().name()))
                                .id(awsEksCluster.getCluster().name())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
