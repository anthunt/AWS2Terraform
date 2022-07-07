package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSRdsOptionGroup;
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
import software.amazon.awssdk.services.rds.model.Option;
import software.amazon.awssdk.services.rds.model.OptionGroup;
import software.amazon.awssdk.services.rds.model.OptionSetting;
import software.amazon.awssdk.services.rds.model.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportRdsOptionGroupsTest {

    private static ExportRdsOptionGroups exportRdsOptionGroups;
    private static RdsClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportRdsOptionGroups = new ExportRdsOptionGroups();
        exportRdsOptionGroups.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getRdsClient();
    }

    private List<AWSRdsOptionGroup> getAwsRdsOptionGroups() {
        return List.of(
                AWSRdsOptionGroup.builder()
                        .optionGroup(
                                OptionGroup.builder()
                                        .optionGroupName("test")
                                        .engineName("mysql")
                                        .majorEngineVersion("5.6")
                                        .optionGroupDescription("test desc")
                                        .options(Option.builder()
                                                .optionName("MARIADB_AUDIT_PLUGIN")
                                                .optionSettings(OptionSetting.builder()
                                                                .name("SERVER_AUDIT_EXCL_USERS")
                                                                .value(null)
                                                                .build(),
                                                        OptionSetting.builder()
                                                                .name("SERVER_AUDIT_EVENTS")
                                                                .value("CONNECT,QUERY")
                                                                .build(),
                                                        OptionSetting.builder()
                                                                .name("SERVER_AUDIT_FILE_PATH")
                                                                .value("/rdsdbdata/log/audit/")
                                                                .build())
                                                .build())
                                        .build()
                        )
                        .tag(Tag.builder().key("Name").value("rds-dev-optiongrp").build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportRdsOptionGroups.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getOptionGroups() {
        List<AWSRdsOptionGroup> awsRdsOptionGroups = exportRdsOptionGroups.listAwsRdsOptionGroups(client);
        log.debug("awsRdsOptionGroups => {}", awsRdsOptionGroups);
    }

    @Test
    public void getResourceMaps() {
        List<AWSRdsOptionGroup> awsRdsOptionGroups = getAwsRdsOptionGroups();

        Maps<Resource> resourceMaps = exportRdsOptionGroups.getResourceMaps(awsRdsOptionGroups);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/RdsOptionGroup.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/RdsOptionGroup.cmd"));
        String actual = exportRdsOptionGroups.getTFImport(getAwsRdsOptionGroups()).script();

        assertEquals(expected, actual);
    }
}