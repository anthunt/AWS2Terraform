package com.anthunt.terraform.generator.aws.service.s3;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.apigateway.model.GetTagsRequest;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeEgressOnlyInternetGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;
import software.amazon.awssdk.services.ec2.model.InternetGatewayAttachment;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetBucketAclRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAclResponse;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.Grant;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.Owner;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ExportS3Buckets extends AbstractExport<S3Client>{
    @Override
    protected Maps<Resource> export(S3Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<Bucket> buckets = getBuckets(client);
        return getResourceMaps(buckets, client);
    }

    protected List<Bucket> getBuckets(S3Client client) {
        ListBucketsResponse describeBucketsResponse = client.listBuckets();
        return describeBucketsResponse.buckets();
    }

    protected Maps<Resource> getResourceMaps(List<Bucket> buckets, S3Client client) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        int i = 0;
        for(Bucket bucket : buckets) {
            GetBucketAclResponse getBucketAclResponse = client.getBucketAcl(GetBucketAclRequest.builder()
                                                                            .bucket(bucket.name())
                                                                            .build());

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_s3_bucket")
                            .name("s3_bucket" + i)
                            .arguments(
                                    TFArguments.builder()
                                            .argument("bucket", TFString.build(bucket.name()))
                                            .argument("acl", TFString.build(getCannedAcl(getBucketAclResponse.grants(), getBucketAclResponse.owner())))
                                            .argument("tags", TFMap.build(
                                                client.getBucketTagging(GetBucketTaggingRequest.builder().bucket(bucket.name()).build()).tagSet().stream()
                                                    .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                            ))
                                            .build()
                            ).build()
            );
            i++;
        }

        return resourceMapsBuilder.build();
    }
    
    protected String getCannedAcl(List<Grant> grants, Owner owner) {
        if(grants.size() == 1 
        && owner.id() == grants.get(0).grantee().id() 
        && grants.get(0).grantee().typeAsString().compareTo("CanonicalUser") == 0
        && grants.get(0).permissionAsString().compareTo("FULL_CONTROL") == 0) {
            return "private";
        }
        else
            return "null";
    }
    
}
