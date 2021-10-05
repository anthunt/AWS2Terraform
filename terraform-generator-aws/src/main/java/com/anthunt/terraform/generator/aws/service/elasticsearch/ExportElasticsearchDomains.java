package com.anthunt.terraform.generator.aws.service.elasticsearch;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.elasticsearch.model.AWSElasticsearchDomain;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.elasticsearch.ElasticsearchClient;
import software.amazon.awssdk.services.elasticsearch.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportElasticsearchDomains extends AbstractExport<ElasticsearchClient> {

    @Override
    protected Maps<Resource> export(ElasticsearchClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSElasticsearchDomain> awsElasticsearchDomains = listAwsElasticsearchDomains(client);

        return getResourceMaps(awsElasticsearchDomains);

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
            ElasticsearchDomainStatus elasticsearchDomainStatus = awsElasticsearchDomain.getElasticsearchDomainStatus();
            List<Tag> tags = awsElasticsearchDomain.getTags();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_elasticsearch_domain")
                            .name(elasticsearchDomainStatus.domainName())
                            .argument("domain_name", TFString.build(elasticsearchDomainStatus.domainName()))
                            .argument("elasticsearch_version", TFString.build(elasticsearchDomainStatus.elasticsearchVersion()))
                            .argument("cluster_config", TFBlock.builder()
                                    .argument("instance_type", TFString.build(
                                            elasticsearchDomainStatus.elasticsearchClusterConfig().instanceTypeAsString()))
                                    .argument("instance_count", TFNumber.build(
                                            elasticsearchDomainStatus.elasticsearchClusterConfig().instanceCount()))
                                    .build())
                            .argument("vpc_options", TFBlock.builder()
                                    .argument("subnet_ids", TFList.build(
                                            elasticsearchDomainStatus.vpcOptions().subnetIds().stream()
                                                    .map(subnetId -> TFString.builder().isLineIndent(false)
                                                            .value(subnetId)
                                                            .build())
                                                    .collect(Collectors.toList())))
                                    .argument("security_group_ids", TFList.build(
                                            elasticsearchDomainStatus.vpcOptions().securityGroupIds().stream()
                                                    .map(securityGroupId -> TFString.builder().isLineIndent(false)
                                                            .value(securityGroupId)
                                                            .build())
                                                    .collect(Collectors.toList())))
                                    .build())
                            .argument("advanced_options", TFMap.build(
                                    elasticsearchDomainStatus.advancedOptions().entrySet().stream()
                                            .collect(Collectors.toMap(Map.Entry::getKey, parameter -> TFString.build(parameter.getValue())))
                            ))
                            .argument("ebs_options", TFBlock.builder()
                                    .argument("ebs_enabled", TFBool.build(
                                            elasticsearchDomainStatus.ebsOptions().ebsEnabled()))
                                    .argument("volume_size", TFNumber.build(
                                            elasticsearchDomainStatus.ebsOptions().volumeSize()))
                                    .argument("volume_type", TFString.build(
                                            elasticsearchDomainStatus.ebsOptions().volumeTypeAsString()))
                                    .build())
                            .argumentsIf(elasticsearchDomainStatus.hasLogPublishingOptions(),
                                    "log_publishing_options",
                                    () -> elasticsearchDomainStatus.logPublishingOptions().entrySet().stream()
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
                            .argument("tags", TFMap.build(
                                    tags.stream()
                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                            ))
                            .build());

        });

        return resourceMapsBuilder.build();
    }

}
