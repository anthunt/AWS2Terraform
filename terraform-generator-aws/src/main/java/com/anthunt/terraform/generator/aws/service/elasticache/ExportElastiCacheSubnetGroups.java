package com.anthunt.terraform.generator.aws.service.elasticache;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.elasticache.model.AWSCacheSubnetGroup;
import com.anthunt.terraform.generator.aws.utils.ThreadUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFExpression;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFList;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.elasticache.ElastiCacheClient;
import software.amazon.awssdk.services.elasticache.model.CacheSubnetGroup;
import software.amazon.awssdk.services.elasticache.model.DescribeCacheSubnetGroupsResponse;
import software.amazon.awssdk.services.elasticache.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.elasticache.model.Tag;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportElastiCacheSubnetGroups extends AbstractExport<ElastiCacheClient> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "ElastiCacheSubnetGroups";

    @Override
    protected Maps<Resource> export(ElastiCacheClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSCacheSubnetGroup> cacheSubnetGroups = listAwsCacheSubnetGroups(client);
        return getResourceMaps(cacheSubnetGroups);
    }

    @Override
    protected TFImport scriptImport(ElastiCacheClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSCacheSubnetGroup> cacheSubnetGroups = listAwsCacheSubnetGroups(client);
        return getTFImport(cacheSubnetGroups);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSCacheSubnetGroup> listAwsCacheSubnetGroups(ElastiCacheClient client) {

        DescribeCacheSubnetGroupsResponse describeCacheSubnetGroupsResponse = client.describeCacheSubnetGroups();
        return describeCacheSubnetGroupsResponse.cacheSubnetGroups().stream()
                .peek(cacheSubnetGroup -> log.debug("cacheSubnetGroup => {}", cacheSubnetGroup))
                .map(cacheSubnetGroup -> {
                    ThreadUtils.sleep(super.getDelayBetweenApis());
                    return AWSCacheSubnetGroup.builder()
                            .cacheSubnetGroup(cacheSubnetGroup)
                            .tags(client.listTagsForResource(ListTagsForResourceRequest.builder()
                                    .resourceName(cacheSubnetGroup.arn()).build()).tagList())
                            .build();
                })
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSCacheSubnetGroup> awsCacheSubnetGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsCacheSubnetGroups.forEach(awsCacheSubnetGroup -> {
            CacheSubnetGroup cacheSubnetGroup = awsCacheSubnetGroup.getCacheSubnetGroup();
            List<Tag> tags = awsCacheSubnetGroup.getTags();
            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api(awsCacheSubnetGroup.getTerraformResourceName())
                                    .name(awsCacheSubnetGroup.getResourceName())
                                    .argument("name", TFString.build(cacheSubnetGroup.cacheSubnetGroupName()))
                                    .argument("subnet_ids", TFList.builder().isLineIndent(false)
                                            .lists(cacheSubnetGroup.subnets().stream()
                                                    .map(subnet -> TFExpression.builder().isLineIndent(false)
                                                            .expression(
                                                                    MessageFormat.format("aws_subnet.{0}.id", subnet.subnetIdentifier()))
                                                            .build())
                                                    .collect(Collectors.toList()))
                                            .build())
                                    .argument("tags", TFMap.build(
                                            tags.stream()
                                                    .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                    ))
                                    .build()
                    )
                    .build();

        });

        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSCacheSubnetGroup> awsCacheSubnetGroups) {
        return TFImport.builder()
                .importLines(awsCacheSubnetGroups.stream()
                        .map(awsCacheSubnetGroup -> TFImportLine.builder()
                                .address(awsCacheSubnetGroup.getTerraformAddress())
                                .id(awsCacheSubnetGroup.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }

}
