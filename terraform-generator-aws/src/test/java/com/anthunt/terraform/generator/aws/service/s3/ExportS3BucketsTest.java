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

    private List<AWSBucket> getAwsBuckets() {
        return List.of(
                AWSBucket.builder()
                        .bucket(Bucket.builder()
                                .name("config-bucket-235090236746")
                                .build())
                        .acl(GetBucketAclResponse.builder()
                                .grants(builder -> builder.grantee(Grantee.builder()
                                                        .id("4ddfa5cd352bef6cf39f9921b7057caa1fbe0b152f0c636a5cff842120455bd2")
                                                        .type("CanonicalUser")
                                                        .build())
                                                .permission("FULL_CONTROL")
                                                .build(),
                                        builder -> builder.grantee(Grantee.builder()
                                                        .type("Group")
                                                        .uri("http://acs.amazonaws.com/groups/s3/LogDelivery")
                                                        .build())
                                                .permission("FULL_CONTROL")
                                                .build()
                                )
                                .build())
                        .policy(TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/input/S3PolicyDocument.json")))
                        .requestPayment(GetBucketRequestPaymentResponse.builder()
                                .payer(Payer.BUCKET_OWNER)
                                .build())
                        .accelerateConfiguration(GetBucketAccelerateConfigurationResponse.builder().build())
                        .build(),
                AWSBucket.builder()
                        .bucket(Bucket.builder()
                                .name("aws-cloudtrail-logs")
                                .build())
                        .acl(GetBucketAclResponse.builder()
                                .grants(Grant.builder().grantee(Grantee.builder()
                                                .id("fb11ff2b1ee34087a15526f5f078675c7bd5cf8c773b3e4797104c91791bb25f")
                                                .type("CanonicalUser")
                                                .build())
                                        .permission("FULL_CONTROL")
                                        .build())
                                .build())
                        .policy(TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/input/S3PolicyDocument.json")))
                        .versioning(GetBucketVersioningResponse.builder()
                                .status(BucketVersioningStatus.ENABLED)
                                .build())
                        .logging(GetBucketLoggingResponse.builder()
                                .loggingEnabled(LoggingEnabled.builder()
                                        .targetBucket("s3-dev-aws-console-log")
                                        .targetPrefix("s3-access-log")
                                        .build())
                                .build())
                        .lifecycleRules(List.of(LifecycleRule.builder()
                                .id("ctail-dev-aws-log-retention-cycle")
                                .filter(LifecycleRuleFilter.builder()
                                        .prefix("ctrail")
                                        .build())
                                .status(ExpirationStatus.ENABLED)
                                .build()))
                        .requestPayment(GetBucketRequestPaymentResponse.builder()
                                .payer(Payer.BUCKET_OWNER)
                                .build())
                        .accelerateConfiguration(GetBucketAccelerateConfigurationResponse.builder().build())
                        .encryption(GetBucketEncryptionResponse.builder()
                                .serverSideEncryptionConfiguration(ServerSideEncryptionConfiguration.builder()
                                        .rules(ServerSideEncryptionRule.builder()
                                                .applyServerSideEncryptionByDefault(builder ->
                                                        builder.kmsMasterKeyID("arn:aws:kms:ap-northeast-2:100020003000:alias/aws/s3")
                                                                .sseAlgorithm("aws:kms").build())
                                                .build())
                                        .build())
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportS3Buckets.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        List<AWSBucket> awsBuckets = getAwsBuckets();

        Maps<Resource> resourceMaps = exportS3Buckets.getResourceMaps(awsBuckets);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/S3Bucket.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/S3Bucket.cmd"));
        String actual = exportS3Buckets.getTFImport(getAwsBuckets()).script();

        assertEquals(expected, actual);
    }
}