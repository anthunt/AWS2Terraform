package com.anthunt.terraform.generator.aws.service.s3.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@Data
@Builder
@ToString
public class AWSBucket {
    private Bucket bucket;
    private GetBucketAclResponse acl;
    private String policy;
    private GetBucketWebsiteResponse website;
    private GetBucketVersioningResponse versioning;
    private GetBucketLoggingResponse logging;
    private List<LifecycleRule> lifecycleRules;
    private GetBucketAccelerateConfigurationResponse accelerateConfiguration;
    private GetBucketRequestPaymentResponse requestPayment;
    private ReplicationConfiguration replication;
    private GetBucketEncryptionResponse encryption;
    private GetObjectLockConfigurationResponse objectLock;
    @Singular
    private List<Tag> tags;
}
