package com.anthunt.terraform.generator.aws.service.efs;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSBackupPolicy;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSEfs;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSFileSystemPolicy;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSMountTarget;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.aws.utils.OptionalUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.efs.EfsClient;
import software.amazon.awssdk.services.efs.model.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportEfs extends AbstractExport<EfsClient> {

    @Override
    protected Maps<Resource> export(EfsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSEfs> awsEfs = listAwsEfs(client);
        return getResourceMaps(awsEfs);
    }

    @Override
    protected TFImport scriptImport(EfsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSEfs> awsEfs = listAwsEfs(client);
        return getTFImport(awsEfs);
    }

    List<AWSEfs> listAwsEfs(EfsClient client) {
        DescribeFileSystemsResponse describeFileSystems = client.describeFileSystems();
        return describeFileSystems.fileSystems().stream()
//                .peek(fileSystem -> log.debug("fileSystem => {}", fileSystem))
                .map(fileSystem -> AWSEfs.builder()
                        .fileSystemDescription(fileSystem)
                        .awsBackupPolicy(OptionalUtils.getExceptionAsOptional(() -> AWSBackupPolicy.builder()
                                        .backupPolicy(client.describeBackupPolicy(DescribeBackupPolicyRequest.builder()
                                                        .fileSystemId(fileSystem.fileSystemId())
                                                        .build())
                                                .backupPolicy()
                                        )
                                        .build())
                                .orElse(null))
                        .awsFileSystemPolicy(OptionalUtils.getExceptionAsOptional(() -> AWSFileSystemPolicy.builder()
                                .fileSystemDescription(fileSystem)
                                .fileSystemPolicy(
                                        URLDecoder.decode(
                                                client.describeFileSystemPolicy(DescribeFileSystemPolicyRequest.builder()
                                                                .fileSystemId(fileSystem.fileSystemId())
                                                                .build())
                                                        .policy()
                                                , StandardCharsets.UTF_8))
                                .build()).orElse(null))
                        .awsMountTargets(OptionalUtils.getExceptionAsOptional(() ->
                                        client.describeMountTargets(DescribeMountTargetsRequest.builder()
                                                        .fileSystemId(fileSystem.fileSystemId())
                                                        .build())
                                                .mountTargets().stream()
                                                .map(mountTarget -> AWSMountTarget.builder()
                                                        .mountTarget(mountTarget)
                                                        .build())
                                                .collect(Collectors.toList()))
                                .orElse(null))
                        .build())
                .peek(AWSEfs -> log.debug("fileSystemPolicy => {}", AWSEfs.getAwsFileSystemPolicy()))
                .collect(Collectors.toList());

    }

    Maps<Resource> getResourceMaps(List<AWSEfs> awsEfses) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSEfs awsEfs : awsEfses) {
            FileSystemDescription fileSystem = awsEfs.getFileSystemDescription();
            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api(awsEfs.getTerraformResourceName())
                                    .name(awsEfs.getResourceName())
                                    .argument("encrypted", TFBool.build(fileSystem.encrypted()))
                                    .argument("kms_key_id", TFString.build(fileSystem.kmsKeyId()))
                                    .argument("performance_mode", TFString.build(fileSystem.performanceModeAsString()))
                                    .argument("throughput_mode", TFString.build(fileSystem.throughputModeAsString()))
                                    .argument("provisioned_throughput_in_mibps",
                                            Optional.ofNullable(fileSystem.provisionedThroughputInMibps())
                                                    .map(TFNumber::build).orElse(TFNumber.build(null)))
                                    .argument("tags", TFMap.build(
                                            fileSystem.tags().stream()
                                                    .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                    ))
                                    .build())
                    .build();
            
            List<AWSMountTarget> awsMountTargets = awsEfs.getAwsMountTargets();
            if (awsMountTargets != null) {
                awsMountTargets.forEach(awsMountTarget -> {
                    MountTargetDescription mountTarget = awsMountTarget.getMountTarget();
                    resourceMapsBuilder.map(
                                            Resource.builder()
                                                    .api(awsMountTarget.getTerraformResourceName())
                                                    .name(awsMountTarget.getResourceName())
                                                    .argument("file_system_id", TFExpression.builder()
                                                            .expression(MessageFormat.format("aws_efs_file_system.{0}.id",
                                                                    awsEfs.getResourceName()))
                                                            .build())
                                                    .argument("subnet_id", TFExpression.builder()
                                                            .expression(MessageFormat.format("aws_subnet.{0}.id", mountTarget.subnetId()))
                                                            .build())
                                                    .build())
                                    .build();
                        }
                );

            }

            AWSFileSystemPolicy awsFileSystemPolicy = awsEfs.getAwsFileSystemPolicy();
            if (awsFileSystemPolicy != null) {
                resourceMapsBuilder.map(
                                Resource.builder()
                                        .api(awsFileSystemPolicy.getTerraformResourceName())
                                        .name(awsFileSystemPolicy.getResourceName())
                                        .argument("file_system_id", TFExpression.builder()
                                                .expression(MessageFormat.format("aws_efs_file_system.{0}.id",
                                                        awsEfs.getResourceName()))
                                                .build())
                                        .argument("policy", TFString.builder().isMultiline(true).value(
                                                        JsonUtils.toPrettyFormat(awsFileSystemPolicy.getFileSystemPolicy()))
                                                .build())
                                        .build())
                        .build();
            }

            AWSBackupPolicy awsBackupPolicy = awsEfs.getAwsBackupPolicy();
            if (Optional.ofNullable(awsBackupPolicy).isPresent()) {
                resourceMapsBuilder.map(
                                Resource.builder()
                                        .api(awsBackupPolicy.getTerraformResourceName())
                                        .name(awsBackupPolicy.getResourceName())
                                        .argument("file_system_id", TFExpression.builder()
                                                .expression(MessageFormat.format("aws_efs_file_system.{0}.id",
                                                        awsEfs.getResourceName()))
                                                .build())
                                        .argument("backup_policy", TFMap.builder()
                                                .map("status", TFString.build(awsBackupPolicy.getBackupPolicy().statusAsString()))
                                                .build())
                                        .build())
                        .build();
            }
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSEfs> awsEfses) {
        TFImport.TFImportBuilder tfImportBuilder = TFImport.builder();
        awsEfses.forEach(awsEfs -> {
            tfImportBuilder.importLine(TFImportLine.builder()
                    .address(awsEfs.getTerraformAddress())
                    .id(awsEfs.getResourceId())
                    .build());

            List<AWSMountTarget> awsMountTargets = awsEfs.getAwsMountTargets();

            if (Optional.ofNullable(awsMountTargets).isPresent()) {
                awsMountTargets.forEach(awsMountTarget -> tfImportBuilder.importLine(TFImportLine.builder()
                        .address(awsMountTarget.getTerraformAddress())
                        .id(awsMountTarget.getResourceId())
                        .build()));
            }

            AWSFileSystemPolicy awsFileSystemPolicy = awsEfs.getAwsFileSystemPolicy();
            if (Optional.ofNullable(awsFileSystemPolicy).isPresent()) {
                tfImportBuilder.importLine(TFImportLine.builder()
                        .address(awsFileSystemPolicy.getTerraformAddress())
                        .id(awsFileSystemPolicy.getResourceId())
                        .build());
            }

            AWSBackupPolicy awsBackupPolicy = awsEfs.getAwsBackupPolicy();
            if (Optional.ofNullable(awsBackupPolicy).isPresent()) {
                tfImportBuilder.importLine(TFImportLine.builder()
                        .address(awsBackupPolicy.getTerraformAddress())
                        .id(awsBackupPolicy.getResourceId())
                        .build());
            }

        });
        return tfImportBuilder.build();
    }
}
