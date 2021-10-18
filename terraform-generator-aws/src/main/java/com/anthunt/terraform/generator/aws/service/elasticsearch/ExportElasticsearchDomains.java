package com.anthunt.terraform.generator.aws.service.elasticsearch;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.elasticsearch.model.AWSElasticsearchDomain;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.elasticsearch.ElasticsearchClient;
import software.amazon.awssdk.services.elasticsearch.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportElasticsearchDomains extends AbstractExport<ElasticsearchClient> {

    @Override
    protected Maps<Resource> export(ElasticsearchClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSElasticsearchDomain> awsElasticsearchDomains = listAwsElasticsearchDomains(client);
        return getResourceMaps(awsElasticsearchDomains);
    }

    @Override
    protected TFImport scriptImport(ElasticsearchClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        //TODO:Need to be implemented
        log.warn("Import Script is not implemented, yet!");
        return TFImport.builder().build();
    }

    List<AWSElasticsearchDomain> listAwsElasticsearchDomains(ElasticsearchClient client) {

        ListDomainNamesResponse domainNamesResponse = client.listDomainNames();
        return domainNamesResponse.domainNames().stream()
                .map(domainInfo -> {
                    ElasticsearchDomainStatus elasticsearchDomainStatus = client.describeElasticsearchDomain(DescribeElasticsearchDomainRequest.builder()
                                    .domainName(domainInfo.domainName()).build())
                            .domainStatus();
                    return AWSElasticsearchDomain.builder()
                            .elasticsearchDomainStatus(elasticsearchDomainStatus)
                            .tags(client.listTags(ListTagsRequest.builder()
                                    .arn(elasticsearchDomainStatus.arn())
                                    .build()).tagList())
                            .build();
                })
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSElasticsearchDomain> awsElasticsearchDomains) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsElasticsearchDomains.stream().forEach(awsElasticsearchDomain -> {
            ElasticsearchDomainStatus domainStatus = awsElasticsearchDomain.getElasticsearchDomainStatus();
            List<Tag> tags = awsElasticsearchDomain.getTags();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_elasticsearch_domain")
                            .name(domainStatus.domainName())
                            .argument("domain_name", TFString.build(domainStatus.domainName()))
                            .argument("elasticsearch_version", TFString.build(domainStatus.elasticsearchVersion()))
                            .argument("cluster_config", TFBlock.builder()
                                    .argumentIf(domainStatus.elasticsearchClusterConfig().dedicatedMasterEnabled(),
                                            "dedicated_master_count",
                                            () -> TFNumber.build(domainStatus.elasticsearchClusterConfig().dedicatedMasterCount()))
                                    .argument("dedicated_master_enabled",
                                            TFBool.build(domainStatus.elasticsearchClusterConfig().dedicatedMasterEnabled()))
                                    .argument("instance_type", TFString.build(
                                            domainStatus.elasticsearchClusterConfig().instanceTypeAsString()))
                                    .argument("instance_count", TFNumber.build(
                                            domainStatus.elasticsearchClusterConfig().instanceCount()))
                                    .build())
                            .argument("vpc_options", TFBlock.builder()
                                    .argument("subnet_ids", TFList.builder().isLineIndent(false).lists(
                                                    domainStatus.vpcOptions().subnetIds().stream()
                                                            .map(subnetId -> TFString.builder().isLineIndent(false)
                                                                    .value(subnetId)
                                                                    .build())
                                                            .collect(Collectors.toList()))
                                            .build())
                                    .argument("security_group_ids", TFList.builder().isLineIndent(false).lists(
                                                    domainStatus.vpcOptions().securityGroupIds().stream()
                                                            .map(securityGroupId -> TFString.builder().isLineIndent(false)
                                                                    .value(securityGroupId)
                                                                    .build())
                                                            .collect(Collectors.toList()))
                                            .build())
                                    .build())
                            .argument("advanced_options", TFMap.build(
                                    domainStatus.advancedOptions().entrySet().stream()
                                            .collect(Collectors.toMap(Map.Entry::getKey, parameter -> TFString.build(parameter.getValue())))
                            ))
                            .argument("ebs_options", TFBlock.builder()
                                    .argument("ebs_enabled", TFBool.build(
                                            domainStatus.ebsOptions().ebsEnabled()))
                                    .argument("volume_size", TFNumber.build(
                                            domainStatus.ebsOptions().volumeSize()))
                                    .argument("volume_type", TFString.build(
                                            domainStatus.ebsOptions().volumeTypeAsString()))
                                    .build())
                            .argumentsIf(domainStatus.hasLogPublishingOptions(),
                                    "log_publishing_options",
                                    () -> domainStatus.logPublishingOptions().entrySet().stream()
                                            .map(option -> TFBlock.builder()
                                                    .argument("cloudwatch_log_group_arn", TFString.builder()
                                                            .value(option.getValue().cloudWatchLogsLogGroupArn())
                                                            .build())
                                                    .argument("enabled", TFBool.builder()
                                                            .bool(option.getValue().enabled())
                                                            .build())
                                                    .argument("log_type", TFString.builder()
                                                            .value(option.getKey().name())
                                                            .build())
                                                    .build())
                                            .collect(Collectors.toList()))
                            .argument("node_to_node_encryption", TFBlock.builder()
                                    .argument("enabled",
                                            TFBool.build(domainStatus.nodeToNodeEncryptionOptions().enabled()))
                                    .build())
                            .argument("encrypt_at_rest", TFBlock.builder()
                                    .argument("enabled",
                                            TFBool.build(domainStatus.encryptionAtRestOptions().enabled()))
                                    .build())
                            .argument("advanced_security_options", TFBlock.builder()
                                    .argument("enabled",
                                            TFBool.build(domainStatus.advancedSecurityOptions().enabled()))
                                    .argument("internal_user_database_enabled",
                                            TFBool.build(domainStatus.advancedSecurityOptions()
                                                    .internalUserDatabaseEnabled()))
                                    .build())
                            .argument("cognito_options", TFBlock.builder()
                                    .argument("enabled",
                                            TFBool.build(domainStatus.cognitoOptions().enabled()))
                                    .argument("identity_pool_id",
                                            TFString.build(domainStatus.cognitoOptions()
                                                    .identityPoolId()))
                                    .argument("role_arn",
                                            TFString.build(domainStatus.cognitoOptions()
                                                    .roleArn()))
                                    .argument("user_pool_id",
                                            TFString.build(domainStatus.cognitoOptions()
                                                    .userPoolId()))
                                    .build())
                            .argument("domain_endpoint_options", TFBlock.builder()
                                    .argument("enforce_https",
                                            TFBool.build(domainStatus.domainEndpointOptions().enforceHTTPS()))
                                    .argument("tls_security_policy",
                                            TFString.build(domainStatus.domainEndpointOptions()
                                                    .tlsSecurityPolicyAsString()))
                                    .build())
                            .argument("ebs_options", TFBlock.builder()
                                    .argument("ebs_enabled",
                                            TFBool.build(domainStatus.ebsOptions().ebsEnabled()))
                                    .argument("iops",
                                            Optional.ofNullable(domainStatus.ebsOptions().iops()).map(iops -> TFNumber.build(iops)).orElse(TFNumber.build(null)))
                                    .argument("volume_size",
                                            TFNumber.build(domainStatus.ebsOptions()
                                                    .volumeSize()))
                                    .argument("volume_type",
                                            TFString.build(domainStatus.ebsOptions()
                                                    .volumeTypeAsString()))
                                    .build())
                            .argument("tags", TFMap.build(
                                    tags.stream()
                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                            ))
                            .build());

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_elasticsearch_domain_policy")
                            .name(domainStatus.domainName())
                            .argument("domain_name", TFString.build(domainStatus.domainName()))
                            .argument("access_policies", TFString.builder()
                                    .isMultiline(true)
                                    .value(JsonUtils.toPrettyFormat(domainStatus.accessPolicies()))
                                    .build())
                            .build());
        });

        return resourceMapsBuilder.build();
    }

}
