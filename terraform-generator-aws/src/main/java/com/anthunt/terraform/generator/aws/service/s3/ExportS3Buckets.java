package com.anthunt.terraform.generator.aws.service.s3;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.s3.model.AWSBucket;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.aws.utils.OptionalUtils;
import com.anthunt.terraform.generator.aws.utils.ThreadUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportS3Buckets extends AbstractExport<S3Client> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "S3Buckets";

    @Override
    protected Maps<Resource> export(S3Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSBucket> repositories = listAwsBuckets(client);
        return getResourceMaps(repositories);
    }

    @Override
    protected TFImport scriptImport(S3Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSBucket> repositories = listAwsBuckets(client);
        return getTFImport(repositories);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSBucket> listAwsBuckets(S3Client client) {
        ListBucketsResponse listBucketsResponse = client.listBuckets();
        return listBucketsResponse.buckets().stream()
                .map(bucket -> {
                    ThreadUtils.sleep(super.getDelayBetweenApis());
                    return AWSBucket.builder()
                            .bucket(bucket)
                            .acl(client.getBucketAcl(GetBucketAclRequest.builder()
                                    .bucket(bucket.name())
                                    .build()))
                            .policy(OptionalUtils.getExceptionAsOptional(() ->
                                    client.getBucketPolicy(GetBucketPolicyRequest.builder()
                                                    .bucket(bucket.name()).build())
                                            .policy()).orElse(null))
                            .website(OptionalUtils.getExceptionAsOptional(() ->
                                    client.getBucketWebsite(GetBucketWebsiteRequest.builder()
                                            .bucket(bucket.name())
                                            .build())).orElse(null)
                            )
                            .versioning(client.getBucketVersioning(GetBucketVersioningRequest.builder()
                                    .bucket(bucket.name()).build()))
                            .logging(client.getBucketLogging(GetBucketLoggingRequest.builder()
                                    .bucket(bucket.name()).build()))
                            .lifecycleRules(OptionalUtils.getExceptionAsOptional(() ->
                                            client.getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest.builder()
                                                    .bucket(bucket.name()).build()))
                                    .map(getBucketLifecycleConfigurationResponse -> getBucketLifecycleConfigurationResponse.rules()).orElse(null)
                            )
                            .accelerateConfiguration(client.getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest.builder()
                                    .bucket(bucket.name()).build()))
                            .requestPayment(client.getBucketRequestPayment(GetBucketRequestPaymentRequest.builder()
                                    .bucket(bucket.name()).build()))
                            .replication(OptionalUtils.getExceptionAsOptional(() ->
                                            client.getBucketReplication(GetBucketReplicationRequest.builder()
                                                    .bucket(bucket.name()).build()))
                                    .map(getBucketReplicationResponse -> getBucketReplicationResponse.replicationConfiguration()).orElse(null))
                            .encryption(OptionalUtils.getExceptionAsOptional(() ->
                                    client.getBucketEncryption(GetBucketEncryptionRequest.builder()
                                            .bucket(bucket.name()).build())).orElse(null))
                            .objectLock(OptionalUtils.getExceptionAsOptional(() ->
                                    client.getObjectLockConfiguration(GetObjectLockConfigurationRequest.builder()
                                            .bucket(bucket.name()).build())).orElse(null))
                            .tags(OptionalUtils.getExceptionAsOptional(() ->
                                            client.getBucketTagging(GetBucketTaggingRequest.builder()
                                                    .bucket(bucket.name())
                                                    .build()))
                                    .map(getBucketTaggingResponse -> getBucketTaggingResponse.tagSet())
                                    .orElse(List.of())
                            )

                            .build();
                })
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSBucket> awsBuckets) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSBucket awsBucket : awsBuckets) {
            Bucket bucket = awsBucket.getBucket();
            GetBucketAclResponse acl = awsBucket.getAcl();
            GetBucketWebsiteResponse bucketWebsite = awsBucket.getWebsite();
            GetBucketVersioningResponse bucketVersioning = awsBucket.getVersioning();
            GetBucketLoggingResponse bucketLogging = awsBucket.getLogging();
            List<LifecycleRule> lifecycleRules = awsBucket.getLifecycleRules();
            log.debug("Optional.ofNullable(lifecycleRules).isPresent()={}", Optional.ofNullable(lifecycleRules).isPresent());
            ReplicationConfiguration replication = awsBucket.getReplication();
            GetBucketEncryptionResponse encryption = awsBucket.getEncryption();
            GetObjectLockConfigurationResponse objectLock = awsBucket.getObjectLock();
            List<Tag> tags = awsBucket.getTags();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api(awsBucket.getTerraformResourceName())
                            .name(awsBucket.getResourceName())
                            .argument("bucket", TFString.build(bucket.name()))
                            .argumentsIf(Optional.ofNullable(acl).isPresent(),
                                    "grant",
                                    () -> acl.grants().stream()
                                            .map(grant -> TFBlock.builder()
                                                    .argumentIf(grant.grantee().typeAsString().equals("CanonicalUser"),
                                                            "id",
                                                            TFString.build(grant.grantee().id()))
                                                    .argument("type", TFString.build(grant.grantee().typeAsString()))
                                                    .argument("permission", TFList.build(
                                                            List.of(TFString.builder().isLineIndent(false)
                                                                    .value(grant.permissionAsString()).build())))
                                                    .argumentIf(grant.grantee().typeAsString().equals("Group"),
                                                            "uri", TFString.build(grant.grantee().uri()))
                                                    .build()
                                            )
                                            .collect(Collectors.toList()))
                            .argumentIf(Optional.ofNullable(bucketWebsite).isPresent(),
                                    "website",
                                    () -> TFBlock.builder()
                                            .argument("index_document", TFString.build(bucketWebsite.indexDocument().toString()))
                                            .argument("error_document", TFString.build(bucketWebsite.errorDocument().toString()))
                                            .argument("routing_rules ", TFList.builder()
                                                    .lists(bucketWebsite.routingRules().stream()
                                                            .map(routingRule -> TFString.builder()
                                                                    .value(routingRule.toString())
                                                                    .build())
                                                            .collect(Collectors.toList()))
                                                    .build())
                                            .build()
                            )
                            .argumentIf(Optional.ofNullable(bucketVersioning).isPresent() &&
                                            Optional.ofNullable(bucketVersioning.status()).isPresent(),
                                    "versioning",
                                    () -> TFBlock.builder()
                                            .argument("enabled", TFBool.build(BucketVersioningStatus.ENABLED == bucketVersioning.status()))
                                            .build())
                            .argumentIf(Optional.ofNullable(bucketLogging).isPresent() &&
                                            Optional.ofNullable(bucketLogging.loggingEnabled()).isPresent(),
                                    "logging",
                                    () -> TFBlock.builder()
                                            .argument("target_bucket", TFString.build(bucketLogging.loggingEnabled().targetBucket()))
                                            .argument("target_prefix", TFString.build(bucketLogging.loggingEnabled().targetPrefix()))
                                            .build())
                            .argumentsIf(Optional.ofNullable(lifecycleRules).isPresent(),
                                    "lifecycle_rule",
                                    () -> lifecycleRules.stream()
                                            .peek(lifecycleRule -> log.debug("lifecycleRule.filter()=>{}", lifecycleRule.filter()))
                                            .map(lifecycleRule -> TFBlock.builder()
                                                    .argument("id", TFString.build(lifecycleRule.id()))
                                                    .argument("prefix", TFString.build(lifecycleRule.filter().prefix()))
                                                    .argumentIf(Optional.ofNullable(lifecycleRule.filter().tag()).isPresent(),
                                                            "tags",
                                                            () -> TFMap.builder()
                                                                    .map(lifecycleRule.filter().tag().key(),
                                                                            TFString.build(lifecycleRule.filter().tag().value()))
                                                                    .build())
                                                    .argumentIf(Optional.ofNullable(lifecycleRule.filter().tag()).isEmpty(),
                                                            "tags",
                                                            TFMap::empty)
                                                    .argument("enabled", TFBool.build(lifecycleRule.status() == ExpirationStatus.ENABLED))
                                                    .argumentIf(Optional.ofNullable(lifecycleRule.abortIncompleteMultipartUpload()).isPresent(),
                                                            "abort_incomplete_multipart_upload_days",
                                                            () -> TFNumber.build(lifecycleRule.abortIncompleteMultipartUpload()
                                                                    .daysAfterInitiation()))
                                                    .argumentIf(Optional.ofNullable(lifecycleRule.expiration()).isPresent(),
                                                            "expiration",
                                                            () -> TFBlock.builder()
                                                                    .argumentIf(Optional.ofNullable(lifecycleRule.expiration().days()).isPresent(),
                                                                            "days",
                                                                            TFNumber.build(lifecycleRule.expiration().days()))
                                                                    .argumentIf(Optional.ofNullable(lifecycleRule.expiration().days()).isPresent(),
                                                                            "date",
                                                                            TFString.build(lifecycleRule.expiration().date().toString()))
                                                                    .build())
                                                    .argumentIf(Optional.ofNullable(lifecycleRule.noncurrentVersionExpiration()).isPresent(),
                                                            "noncurrent_version_expiration",
                                                            () -> TFObject.builder().member("days",
                                                                            TFNumber.build(lifecycleRule.noncurrentVersionExpiration().noncurrentDays()))
                                                                    .build())
                                                    .argumentsIf(Optional.ofNullable(lifecycleRule.noncurrentVersionTransitions()).isPresent(),
                                                            "noncurrent_version_transition",
                                                            lifecycleRule.noncurrentVersionTransitions().stream()
                                                                    .map(transition -> TFObject.builder()
                                                                            .member("days", TFNumber.build(transition.noncurrentDays()))
                                                                            .member("storage_class", TFString.build(transition.storageClassAsString()))
                                                                            .build())
                                                                    .collect(Collectors.toList()))
                                                    .build())
                                            .collect(Collectors.toList()))

                            .argument("acceleration_status", TFString.builder().value(awsBucket.getAccelerateConfiguration().statusAsString()).build())
                            .argument("request_payer", TFString.builder().value(awsBucket.getRequestPayment().payerAsString()).build())
                            .argumentIf(Optional.ofNullable(replication).isPresent(),
                                    "replication_configuration",
                                    () -> {
                                        List<ReplicationRule> rules = replication.rules();
                                        return TFBlock.builder()
                                                .argument("role", TFString.builder().value(replication.role()).build())
                                                .argumentsIf(Optional.ofNullable(rules).isPresent(),
                                                        "rules",
                                                        rules.stream()
                                                                .map(rule -> TFBlock.builder()
                                                                        .argument("id", TFString.build(rule.id()))
                                                                        .argument("prefix", TFString.build(rule.filter().prefix()))
                                                                        .argument("status", TFString.build(rule.status().toString()))
                                                                        .argument("destination", TFBlock.builder()
                                                                                .argument("bucket", TFString.build(rule.destination().bucket()))
                                                                                .argument("storage_class", TFString.build(rule.destination().storageClassAsString()))
                                                                                .build())
                                                                        .build())
                                                                .collect(Collectors.toList()))
                                                .build();
                                    })
                            .argumentIf(Optional.ofNullable(encryption).isPresent(),
                                    "server_side_encryption_configuration",
                                    () -> {
                                        List<ServerSideEncryptionRule> rules = encryption.serverSideEncryptionConfiguration().rules();
                                        return TFBlock.builder()
                                                .argumentsIf(Optional.ofNullable(rules).isPresent(),
                                                        "rule",
                                                        () -> rules.stream()
                                                                .map(rule -> TFBlock.builder()
                                                                        .argumentIf(Optional.ofNullable(rule.applyServerSideEncryptionByDefault()).isPresent(),
                                                                                "apply_server_side_encryption_by_default",
                                                                                TFBlock.builder()
                                                                                        .argument("kms_master_key_id", TFString.build(rule.applyServerSideEncryptionByDefault().kmsMasterKeyID()))
                                                                                        .argument("sse_algorithm", TFString.build(rule.applyServerSideEncryptionByDefault().sseAlgorithmAsString()))
                                                                                        .build())
                                                                        .build())
                                                                .collect(Collectors.toList()))
                                                .build();
                                    })
                            .argumentIf(Optional.ofNullable(objectLock).isPresent(),
                                    "object_lock_configuration",
                                    () -> {
                                        ObjectLockConfiguration objectLockConfiguration = objectLock.objectLockConfiguration();
                                        return TFBlock.builder()
                                                .argument("object_lock_enabled", TFString.build(objectLockConfiguration.objectLockEnabledAsString()))
                                                .argumentIf(Optional.ofNullable(objectLockConfiguration.rule()).isPresent(),
                                                        "rule",
                                                        () -> {
                                                            DefaultRetention defaultRetention = objectLockConfiguration.rule().defaultRetention();
                                                            return TFBlock.builder()
                                                                    .argument("default_retention", TFBlock.builder()
                                                                            .argument("mode", TFString.build(defaultRetention.modeAsString()))
                                                                            .argumentIf(Optional.ofNullable(defaultRetention.days()).isPresent(),
                                                                                    "days",
                                                                                    TFNumber.build(defaultRetention.days()))
                                                                            .argumentIf(Optional.ofNullable(defaultRetention.years()).isPresent(),
                                                                                    "years",
                                                                                    TFNumber.build(defaultRetention.years()))
                                                                            .build())
                                                                    .build();
                                                        })
                                                .build();
                                    })
                            .argumentIf(Optional.ofNullable(awsBucket.getPolicy()).isPresent(),
                                    "policy",
                                    () -> TFString.builder().isMultiline(true)
                                            .value(JsonUtils.toPrettyFormat(awsBucket.getPolicy()))
                                            .build())
                            .argument("tags", TFMap.build(
                                    tags.stream()
                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                            ))
                            .build());
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSBucket> awsBuckets) {
        return TFImport.builder()
                .importLines(awsBuckets.stream()
                        .map(awsBucket -> TFImportLine.builder()
                                .address(awsBucket.getTerraformAddress())
                                .id(awsBucket.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
