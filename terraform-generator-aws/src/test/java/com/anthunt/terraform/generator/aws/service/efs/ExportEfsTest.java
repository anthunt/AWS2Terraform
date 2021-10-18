package com.anthunt.terraform.generator.aws.service.efs;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSEfs;
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
import software.amazon.awssdk.services.efs.EfsClient;
import software.amazon.awssdk.services.efs.model.FileSystemDescription;
import software.amazon.awssdk.services.efs.model.MountTargetDescription;
import software.amazon.awssdk.services.efs.model.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportEfsTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportEfs exportEfs;

    private static EfsClient client;

    @BeforeAll
    public static void beforeAll() {
        exportEfs = new ExportEfs();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getEfsClient();
    }

    private List<AWSEfs> getAwsEfs() {
        return List.of(
                AWSEfs.builder().fileSystemDescription(FileSystemDescription.builder()
                                .fileSystemId("fs-5af144c6")
                                .encrypted(true)
                                .kmsKeyId("arn:aws:kms:ap-northeast-2:100020003000:key/1000ffff-2210-472f-ad29-2c2f4ef0c4e2")
                                .performanceMode("generalPurpose")
                                .throughputMode("bursting")
                                .provisionedThroughputInMibps(null)
                                .tags(Tag.builder().key("Name").value("efs-test-app").build(),
                                        Tag.builder().key("aws:elasticfilesystem:default-backup").value("enabled").build())
                                .build())
                        .backupPolicyStatus("ENABLED")
                        .fileSystemPolicy(TestDataFileUtils.asString(
                                resourceLoader.getResource("testData/aws/input/FileSystemPolicyDocument.json")))
                        .mountTargets(List.of(
                                MountTargetDescription.builder()
                                        .mountTargetId("fsmt-01020304")
                                        .subnetId("subnet-01020304")
                                        .build(),
                                MountTargetDescription.builder()
                                        .mountTargetId("fsmt-02030405")
                                        .subnetId("subnet-02020304")
                                        .build()
                        ))
                        .build(),
                AWSEfs.builder().fileSystemDescription(FileSystemDescription.builder()
                                .fileSystemId("fs-6fa144c6")
                                .encrypted(false)
                                .kmsKeyId(null)
                                .performanceMode("generalPurpose")
                                .throughputMode("bursting")
                                .provisionedThroughputInMibps(null)
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportEfs.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        Maps<Resource> resourceMaps = exportEfs.getResourceMaps(getAwsEfs());
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/Efs.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/Efs.cmd"));
        String actual = exportEfs.getTFImport(getAwsEfs()).script();

        assertEquals(expected, actual);
    }

}