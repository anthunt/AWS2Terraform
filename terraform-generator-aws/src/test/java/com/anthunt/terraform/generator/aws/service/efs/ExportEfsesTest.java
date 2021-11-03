package com.anthunt.terraform.generator.aws.service.efs;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSBackupPolicy;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSEfs;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSFileSystemPolicy;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSMountTarget;
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
import software.amazon.awssdk.services.efs.model.BackupPolicy;
import software.amazon.awssdk.services.efs.model.FileSystemDescription;
import software.amazon.awssdk.services.efs.model.MountTargetDescription;
import software.amazon.awssdk.services.efs.model.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportEfsesTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportEfses exportEfses;

    private static EfsClient client;

    @BeforeAll
    public static void beforeAll() {
        exportEfses = new ExportEfses();
        exportEfses.setDelayBetweenApis(0);
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getEfsClient();
    }

    private List<AWSEfs> getAwsEfs() {
        FileSystemDescription fileSystemDescription1 = FileSystemDescription.builder()
                .fileSystemId("fs-5af144c6")
                .encrypted(true)
                .kmsKeyId("arn:aws:kms:ap-northeast-2:100020003000:key/1000ffff-2210-472f-ad29-2c2f4ef0c4e2")
                .performanceMode("generalPurpose")
                .throughputMode("bursting")
                .provisionedThroughputInMibps(null)
                .tags(Tag.builder().key("Name").value("efs-test-app").build(),
                        Tag.builder().key("aws:elasticfilesystem:default-backup").value("enabled").build())
                .build();
        FileSystemDescription fileSystemDescription2 = FileSystemDescription.builder()
                .fileSystemId("fs-6fa144c6")
                .encrypted(false)
                .kmsKeyId(null)
                .performanceMode("generalPurpose")
                .throughputMode("bursting")
                .provisionedThroughputInMibps(null)
                .build();
        return List.of(
                AWSEfs.builder().fileSystemDescription(fileSystemDescription1)
                        .awsBackupPolicy(AWSBackupPolicy.builder()
                                .fileSystemDescription(fileSystemDescription1)
                                .backupPolicy(BackupPolicy.builder()
                                        .status("ENABLED")
                                        .build()).build())
                        .awsFileSystemPolicy(AWSFileSystemPolicy.builder()
                                .fileSystemDescription(fileSystemDescription1)
                                .fileSystemPolicy(TestDataFileUtils.asString(
                                        resourceLoader.getResource("testData/aws/input/FileSystemPolicyDocument.json")))
                                .build())
                        .awsMountTargets(List.of(
                                AWSMountTarget.builder()
                                        .mountTarget(MountTargetDescription.builder()
                                                .mountTargetId("fsmt-01020304")
                                                .subnetId("subnet-01020304")
                                                .build())
                                        .build(),
                                AWSMountTarget.builder()
                                        .mountTarget(MountTargetDescription.builder()
                                                .mountTargetId("fsmt-02030405")
                                                .subnetId("subnet-02020304")
                                                .build())
                                        .build()
                        ))
                        .build(),
                AWSEfs.builder().fileSystemDescription(fileSystemDescription2)
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportEfses.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        Maps<Resource> resourceMaps = exportEfses.getResourceMaps(getAwsEfs());
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
        String actual = exportEfses.getTFImport(getAwsEfs()).script();

        assertEquals(expected, actual);
    }

}