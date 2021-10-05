package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSRdsClusterParameterGroup;
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
import software.amazon.awssdk.services.rds.model.DBClusterParameterGroup;
import software.amazon.awssdk.services.rds.model.Parameter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportRdsClusterParameterGroupsTest {

    private static ExportRdsClusterParameterGroups exportRdsClusterParameterGroups;
    private static RdsClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportRdsClusterParameterGroups = new ExportRdsClusterParameterGroups();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getRdsClient();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportRdsClusterParameterGroups.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getDBClusterParameterGroups() {
        List<AWSRdsClusterParameterGroup> awsDbClusterParameterGroups = exportRdsClusterParameterGroups.listAwsRdsClusterParameterGroups(client);
        log.debug("awsDbClusterParameterGroups => {}", awsDbClusterParameterGroups);
    }

    @Test
    public void getResourceMaps() {
        List<AWSRdsClusterParameterGroup> awsRdsClusterParameterGroups = List.of(
                AWSRdsClusterParameterGroup.builder()
                        .dbClusterParameterGroup(DBClusterParameterGroup.builder()
                                .dbClusterParameterGroupName("rds-dev-paramgrp")
                                .dbParameterGroupFamily("aurora-mysql5.7")
                                .description("Staging Aurora(Mysql 5.7) Cluster Parameter Group")
                                .build())
                        .parameters(List.of(
                                Parameter.builder()
                                        .parameterName("character_set_client")
                                        .parameterValue("utf8")
                                        .source("modified")
                                        .build(),
                                Parameter.builder()
                                        .parameterName("character_set_connection")
                                        .parameterValue("utf8")
                                        .source("modified")
                                        .build(),
                                Parameter.builder()
                                        .parameterName("aurora_load_from_s3_role")
                                        .parameterValue(null)
                                        .source("engine-default")
                                        .build())
                        )
                        .build()
        );

        Maps<Resource> resourceMaps = exportRdsClusterParameterGroups.getResourceMaps(awsRdsClusterParameterGroups);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/RdsClusterParameterGroup.tf")
        );
        assertEquals(expected, actual);

    }
}