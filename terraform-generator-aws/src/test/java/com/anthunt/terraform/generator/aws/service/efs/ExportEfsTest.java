package com.anthunt.terraform.generator.aws.service.efs;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.efs.dto.EfsDto;
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
import software.amazon.awssdk.services.efs.model.Tag;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportEfsTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportEfs exportEfs;

    @BeforeAll
    public static void beforeAll() {
        exportEfs = new ExportEfs();
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("ulsp-dev").region(Region.AP_NORTHEAST_2).build();
        EfsClient efsClient = amazonClients.getEfsClient();
        Maps<Resource> export = exportEfs.export(efsClient, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        List<EfsDto> efsDtos = List.of(
                EfsDto.builder().fileSystemDescription(FileSystemDescription.builder()
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
                                        resourceLoader.getResource("testData/efs/input/FileSystemPolicyDocument.json")))
                        .build(),
                EfsDto.builder().fileSystemDescription(FileSystemDescription.builder()
                                .encrypted(false)
                                .kmsKeyId(null)
                                .performanceMode("generalPurpose")
                                .throughputMode("bursting")
                                .provisionedThroughputInMibps(null)
                                .build())
                        .build()
        );

        Maps<Resource> resourceMaps = exportEfs.getResourceMaps(efsDtos);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/efs/expected/Efs.tf")
        );
        assertEquals(expected, actual);
    }

}