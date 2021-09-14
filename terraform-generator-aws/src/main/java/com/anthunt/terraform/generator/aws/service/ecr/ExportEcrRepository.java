package com.anthunt.terraform.generator.aws.service.ecr;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.DescribeRepositoriesResponse;
import software.amazon.awssdk.services.ecr.model.Repository;
import software.amazon.awssdk.services.iam.model.Role;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
public class ExportEcrRepository extends AbstractExport<EcrClient> {

    @Override
    protected Maps<Resource> export(EcrClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<Repository> policies = getEcrRepositories(client);

        return getResourceMaps(policies);

    }

    List<Repository> getEcrRepositories(EcrClient client) {
        DescribeRepositoriesResponse describeRepositoriesResponse = client.describeRepositories();
        return describeRepositoriesResponse.repositories();
    }

    Maps<Resource> getResourceMaps(List<Repository> repositories) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (Repository repository:repositories) {

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_ecr_repository")
                            .name(repository.repositoryName().replaceAll("/", "-"))
                            .arguments(
                                    TFArguments.builder()
                                            .argument("name", TFString.build(repository.repositoryName()))
                                            .argument("encryption_configuration", TFMap.builder()
                                                    .map("encryption_type", TFString.build(repository.encryptionConfiguration().encryptionType().toString()))
                                                    .map("kms_key", TFString.build(repository.encryptionConfiguration().kmsKey()))
                                                    .build())
                                            .argument("image_tag_mutability", TFString.build(repository.imageTagMutability().toString()))
                                            .argument("image_scanning_configuration", TFMap.builder()
                                                    .map("scan_on_push", TFBool.build(repository.imageScanningConfiguration().scanOnPush()))
                                                    .build())
                                            .build())
                            .build())
                    .build();
        }
        return resourceMapsBuilder.build();
    }
}
