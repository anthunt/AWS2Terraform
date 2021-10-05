package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSRdsCluster;
import com.anthunt.terraform.generator.aws.support.DisabledOnNoAwsCredentials;
import com.anthunt.terraform.generator.aws.support.TestDataFileUtils;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportRdsClustersTest {

    private static ExportRdsClusters exportRdsClusters;
    private static RdsClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportRdsClusters = new ExportRdsClusters();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getRdsClient();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportRdsClusters.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getDBClusters() {
        List<AWSRdsCluster> awsRdsClusters = exportRdsClusters.listAwsRdsClusters(client);
        log.debug("awsdbClusters => {}", awsRdsClusters);
    }

    @Test
    public void getResourceMaps() {
        List<AWSRdsCluster> awsTargetGroups = List.of(
                AWSRdsCluster.builder()
                        .dbCluster(DBCluster.builder()
                                .databaseName("rds-dev")
                                .dbClusterIdentifier("rds-dev-cluster")
                                .engine("aurora-postgresql")
                                .engineVersion("11.9")
                                .engineMode("provisioned")
                                .availabilityZones("ap-northeast-2a", "ap-northeast-2c", "ap-northeast-2d")
                                .masterUsername("admin")
                                .dbClusterParameterGroup("default.aurora-postgresql11")
                                .dbSubnetGroup("rdsgrp-dev")
                                .port(5432)
                                .storageEncrypted(true)
                                .kmsKeyId("arn:aws:kms:ap-northeast-2:100020003000:key/c1000fcd-2000-3000-4100-500096006000")
                                .vpcSecurityGroups(VpcSecurityGroupMembership.builder().vpcSecurityGroupId("sg-1000200079284a471").build())
                                .backupRetentionPeriod(7)
                                .copyTagsToSnapshot(true)
                                .deletionProtection(true)
                                .tagList(Tag.builder().key("Name").value("rds-dev").build())
                                .build())
                        .dbClusterInstances(List.of(DBInstance.builder()
                                .dbInstanceIdentifier("rds-dev-cluster-instance-1")
                                .dbClusterIdentifier("rds-dev-cluster")
                                .availabilityZone("ap-northeast-2c")
                                .dbInstanceClass("db.t3.medium")
                                .engine("aurora-postgresql")
                                .engineVersion("11.9")
                                .dbSubnetGroup(DBSubnetGroup.builder().dbSubnetGroupName("rdsgrp-dev").build())
                                .monitoringInterval(60)
                                .monitoringRoleArn("arn:aws:iam::100020003000:role/rds-monitoring-role")
                                .performanceInsightsEnabled(true)
                                .tagList(Tag.builder().key("Name").value("rds-dev").build())
                                .build()))
                        .build()
        );

        Maps<Resource> resourceMaps = exportRdsClusters.getResourceMaps(awsTargetGroups);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/rds/expected/rds.tf")
        );
        assertEquals(expected, actual);

    }
}