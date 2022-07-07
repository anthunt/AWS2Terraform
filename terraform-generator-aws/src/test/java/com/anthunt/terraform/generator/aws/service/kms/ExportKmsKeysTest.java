package com.anthunt.terraform.generator.aws.service.kms;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.kms.model.AWSKmsAlias;
import com.anthunt.terraform.generator.aws.service.kms.model.AWSKmsKey;
import com.anthunt.terraform.generator.aws.service.kms.model.AWSKmsKeyPolicy;
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
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.AliasListEntry;
import software.amazon.awssdk.services.kms.model.KeyMetadata;
import software.amazon.awssdk.services.kms.model.KeySpec;
import software.amazon.awssdk.services.kms.model.KeyUsageType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportKmsKeysTest {

    private static ExportKmsKeys exportKmsKeys;

    @Autowired
    private ResourceLoader resourceLoader;

    private static KmsClient client;

    @BeforeAll
    public static void beforeAll() {
        exportKmsKeys = new ExportKmsKeys();
        exportKmsKeys.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getKmsClient();
    }

    private List<AWSKmsKey> getAwsKmsKeys() {
        return List.of(
                AWSKmsKey.builder()
                        .keyMetadata(KeyMetadata.builder()
                                .description("DEV-RDS-KMS")
                                .keyId("01c1b1cd-15dc-4e15-84d7-4ee98788fe5e")
                                .keyUsage(KeyUsageType.ENCRYPT_DECRYPT)
                                .keySpec(KeySpec.SYMMETRIC_DEFAULT)
                                .build())
                        .awsKeyPolicy(AWSKmsKeyPolicy.builder()
                                .policy(TestDataFileUtils.asString(
                                        resourceLoader.getResource("testData/aws/input/KmsKeyPolicyDocument.json")))
                                .build())
                        .awsKmsAlias(AWSKmsAlias.builder()
                                .alias(AliasListEntry.builder()
                                        .aliasName("alias/DEV-RDS-KMS")
                                        .targetKeyId("13c8b4cd-15dc-4e15-84d7-4ee98788fe5e")
                                        .build())
                                .build())
                        .build());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> resourceMaps = exportKmsKeys.export(client, null, null);
        log.debug("export => \n{}", resourceMaps.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void listKeys() {
        List<AWSKmsKey> awsKmsKeys = exportKmsKeys.listKeys(client);
        log.debug("awsKmsKeys => {}", awsKmsKeys);
    }

    @Test
    public void getResourceMaps() {
        List<AWSKmsKey> roles = getAwsKmsKeys();
        Maps<Resource> resourceMaps = exportKmsKeys.getResourceMaps(roles);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/KmsKey.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/KmsKey.cmd"));
        String actual = exportKmsKeys.getTFImport(getAwsKmsKeys()).script();

        assertEquals(expected, actual);
    }
}