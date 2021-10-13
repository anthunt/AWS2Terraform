package com.anthunt.terraform.generator.aws.service.s3;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.s3.model.AWSBucket;
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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportS3BucketsTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportS3Buckets exportS3Buckets;

    private static S3Client client;

    @BeforeAll
    public static void beforeAll() {
        exportS3Buckets = new ExportS3Buckets();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getS3Client();
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportS3Buckets.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        List<AWSBucket> awsBuckets = List.of(
                AWSBucket.builder()
                        .bucket(Bucket.builder()
                                .name("config-bucket-235090236746")
                                .build())
                        .acl(GetBucketAclResponse.builder()
                                .grants(Grant.builder().grantee(Grantee.builder()
                                                .id("4ddfa5cd352bef6cf39f9921b7057caa1fbe0b152f0c636a5cff842120455bd2")
                                                .type("CanonicalUser")
                                                .build())
                                        .permission("FULL_CONTROL")
                                        .build())
                                .build())
                        .policy(TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/input/S3PolicyDocument.json")))
                        .requestPayment(GetBucketRequestPaymentResponse.builder()
                                .payer(Payer.BUCKET_OWNER)
                                .build())
                        .accelerateConfiguration(GetBucketAccelerateConfigurationResponse.builder().build())
                        .build());

        Maps<Resource> resourceMaps = exportS3Buckets.getResourceMaps(awsBuckets);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/S3Bucket.tf")
        );
        assertEquals(expected, actual);
    }

}