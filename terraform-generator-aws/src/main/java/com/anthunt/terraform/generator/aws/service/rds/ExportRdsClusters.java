package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSDBCluster;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rds.model.DescribeDbClustersResponse;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportRdsClusters extends AbstractExport<RdsClient> {

    @Override
    protected Maps<Resource> export(RdsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSDBCluster> awsDbClusters = getDBClusters(client);

        return getResourceMaps(awsDbClusters);

    }

    List<AWSDBCluster> getDBClusters(RdsClient client) {

        DescribeDbClustersResponse describeDbClustersResponse = client.describeDBClusters();
        return describeDbClustersResponse.dbClusters().stream()
                .map(dbCuster -> AWSDBCluster.builder()
                        .dbCluster(dbCuster)
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSDBCluster> awsDbClusters) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsDbClusters.stream().forEach(awsDbCluster -> {
            DBCluster dbCluster = awsDbCluster.getDbCluster();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_rds_cluster")
                            .name(dbCluster.databaseName())
                            .argument("cluster_identifier", TFString.build(dbCluster.dbClusterIdentifier()))
                            .argument("engine", TFString.build(dbCluster.engine()))
                            .argument("engine_version", TFString.build(dbCluster.engineVersion()))
                            .argument("engine_mode", TFString.build(dbCluster.engineMode()))
                            .argument("availability_zones", TFList.build(dbCluster.availabilityZones().stream()
                                    .map(az -> TFString.builder().isLineIndent(false).value(az).build())
                                    .collect(Collectors.toList())))
                            .argument("database_name", TFString.build(dbCluster.databaseName()))
                            .argument("master_username", TFString.build(dbCluster.masterUsername()))
                            .argument("db_cluster_parameter_group_name", TFString.build(dbCluster.dbClusterParameterGroup()))
                            .argument("db_subnet_group_name", TFString.build(dbCluster.dbSubnetGroup()))
                            .argument("port", TFString.build(dbCluster.port().toString()))
                            .argument("storage_encrypted", TFBool.build(dbCluster.storageEncrypted()))
                            .argument("kms_key_id", TFString.build(dbCluster.kmsKeyId()))
                            .argument("vpc_security_group_ids", TFList.build(dbCluster.vpcSecurityGroups().stream()
                                    .map(sg -> TFExpression.build(
                                            MessageFormat.format("aws_security_group.security_groups.{0}.id", sg.vpcSecurityGroupId())))
                                    .collect(Collectors.toList())))
                            .argument("backtrack_window", TFNumber.build(Optional.ofNullable(dbCluster.backtrackWindow())
                                    .map(v -> v.toString()).orElse(null)))
                            .argument("backup_retention_period", TFNumber.build(dbCluster.backupRetentionPeriod().toString()))
                            .argument("copy_tags_to_snapshot", TFBool.build(dbCluster.copyTagsToSnapshot()))
                            .argument("deletion_protection", TFBool.build(dbCluster.deletionProtection()))
//                            .argument("tags", TFMap.build(
//                                    dbCluster.tagList().stream()
//                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
//                            ))
                            .build());
        });

        return resourceMapsBuilder.build();
    }

}
