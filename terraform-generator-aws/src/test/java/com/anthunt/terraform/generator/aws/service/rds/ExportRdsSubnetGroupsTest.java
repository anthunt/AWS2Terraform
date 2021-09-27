package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSDBSubnetGroup;
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
import software.amazon.awssdk.services.rds.model.DBSubnetGroup;
import software.amazon.awssdk.services.rds.model.Subnet;
import software.amazon.awssdk.services.rds.model.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportRdsSubnetGroupsTest {

    private static ExportRdsSubnetGroups exportRdsSubnetGroups;
    private static RdsClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportRdsSubnetGroups = new ExportRdsSubnetGroups();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getRdsClient();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportRdsSubnetGroups.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getDBSubnetGroups() {
        List<AWSDBSubnetGroup> awsDbClusterParameterGroups = exportRdsSubnetGroups.getDBSubnetGroups(client);
        log.debug("awsDbClusterParameterGroups => {}", awsDbClusterParameterGroups);
    }

    @Test
    public void getResourceMaps() {
        List<AWSDBSubnetGroup> dbSubnetGroups = List.of(
                AWSDBSubnetGroup.builder()
                        .dbSubnetGroup(
                                DBSubnetGroup.builder()
                                        .dbSubnetGroupName("rds-dev-subnetgrp")
                                        .subnets(Subnet.builder().subnetIdentifier("subnet-000140c12f7a1ca6e").build(),
                                                Subnet.builder().subnetIdentifier("subnet-000240c12f7a1ca6e").build()
                                        )
                                        .build())
                        .tag(Tag.builder().key("Name").value("rds-dev-subnetgrp").build())
                        .build()
        );

        Maps<Resource> resourceMaps = exportRdsSubnetGroups.getResourceMaps(dbSubnetGroups);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/rds/expected/RdsSubnetGroup.tf")
        );
        assertEquals(expected, actual);

    }
}