package com.anthunt.terraform.generator.aws.service.msk;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.msk.model.AWSMskCluster;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.kafka.KafkaClient;
import software.amazon.awssdk.services.kafka.model.ClusterInfo;
import software.amazon.awssdk.services.kafka.model.ListClustersResponse;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportMskClusters extends AbstractExport<KafkaClient> {

    @Override
    protected Maps<Resource> export(KafkaClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSMskCluster> awsMskClusters = listAwsMskClusters(client);
        return getResourceMaps(awsMskClusters);
    }

    @Override
    protected TFImport scriptImport(KafkaClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        //TODO:Need to be implemented
        log.warn("Import Script is not implemented, yet!");
        return TFImport.builder().build();
    }

    List<AWSMskCluster> listAwsMskClusters(KafkaClient client) {
        ListClustersResponse listClustersResponse = client.listClusters();
        return listClustersResponse.clusterInfoList().stream()
                .map(clusterInfo ->
                        AWSMskCluster.builder()
                                .clusterInfo(clusterInfo)
                                .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSMskCluster> awsMskClusters) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSMskCluster awsMskCluster : awsMskClusters) {
            ClusterInfo cluster = awsMskCluster.getClusterInfo();
//            Map<String, String> tags = awsEksCluster.getTags();
            resourceMapsBuilder
                    .map(Resource.builder()
                            .api("aws_msk_cluster")
                            .name(cluster.clusterName())
                            .argument("cluster_name", TFString.build(cluster.clusterName()))
                            .argument("kafka_version", TFString.build(cluster.currentBrokerSoftwareInfo().kafkaVersion()))
                            .argument("number_of_broker_nodes", TFNumber.build(cluster.numberOfBrokerNodes()))
                            .argument("encryption_info", TFBlock.builder()
                                    .argument("encryption_at_rest_kms_key_arn", TFString.build(cluster.encryptionInfo().encryptionAtRest().dataVolumeKMSKeyId()))
                                    .argument("encryption_in_transit", TFBlock.builder()
                                            .argument("in_cluster", TFBool.build(cluster.encryptionInfo().encryptionInTransit().inCluster()))
                                            .build())
                                    .build())
                            .argument("broker_node_group_info", TFBlock.builder()
                                    .argument("client_subnets", TFList.build(cluster.brokerNodeGroupInfo().clientSubnets().stream()
                                            .map(subnetId -> TFExpression.builder().isLineIndent(false).expression(
                                                            MessageFormat.format("aws_subnet.{0}.id", subnetId))
                                                    .build())
                                            .collect(Collectors.toList())))
                                    .argument("ebs_volume_size", TFNumber.build(cluster.brokerNodeGroupInfo()
                                            .storageInfo().ebsStorageInfo().volumeSize()))
                                    .argument("instance_type", TFString.build(cluster.brokerNodeGroupInfo().instanceType()))
                                    .argument("security_groups", TFList.build(cluster.brokerNodeGroupInfo().securityGroups().stream()
                                            .map(sg -> TFExpression.builder().isLineIndent(false).expression(
                                                            MessageFormat.format("aws_security_group.{0}.id", sg))
                                                    .build())
                                            .collect(Collectors.toList())))
                                    .argument("tags", TFMap.build(
                                            cluster.tags().entrySet().stream()
                                                    .collect(Collectors.toMap(Map.Entry::getKey, tag -> TFString.build(tag.getValue())))
                                    ))
                                    .build())
                            .build())
                    .build();

        }
        return resourceMapsBuilder.build();
    }
}
