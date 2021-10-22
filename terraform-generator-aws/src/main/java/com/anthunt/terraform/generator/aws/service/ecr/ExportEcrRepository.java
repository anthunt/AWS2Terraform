package com.anthunt.terraform.generator.aws.service.ecr;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.ecr.model.AWSRepository;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFBool;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.DescribeRepositoriesResponse;
import software.amazon.awssdk.services.ecr.model.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportEcrRepository extends AbstractExport<EcrClient> {

    @Override
    protected Maps<Resource> export(EcrClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRepository> awsRepositories = listAwsRepositories(client);
        return getResourceMaps(awsRepositories);
    }

    @Override
    protected TFImport scriptImport(EcrClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRepository> awsRepositories = listAwsRepositories(client);
        return getTFImport(awsRepositories);
    }

    List<AWSRepository> listAwsRepositories(EcrClient client) {
        DescribeRepositoriesResponse describeRepositoriesResponse = client.describeRepositories();
        return describeRepositoriesResponse.repositories().stream()
                .map(repository -> AWSRepository.builder()
                        .repository(repository)
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSRepository> awsRepositories) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSRepository awsRepository : awsRepositories) {
            Repository repository = awsRepository.getRepository();
            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api(awsRepository.getTerraformResourceName())
                                    .name(awsRepository.getResourceName())
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
                    .build();
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSRepository> awsRepositories) {
        return TFImport.builder()
                .importLines(awsRepositories.stream()
                        .map(awsRepository -> TFImportLine.builder()
                                .address(awsRepository.getTerraformAddress())
                                .id(awsRepository.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
