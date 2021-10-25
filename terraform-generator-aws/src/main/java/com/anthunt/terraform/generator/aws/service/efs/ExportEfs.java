package com.anthunt.terraform.generator.aws.service.efs;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.efs.model.AWSEfs;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
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
                        .backupPolicyStatus(getBackupPolicyStatus(client, fileSystem.fileSystemId()))
                        .fileSystemPolicy(getFileSystemPolicy(client, fileSystem.fileSystemId()))
                        .mountTargets(getMountTargets(client, fileSystem.fileSystemId()))
                        .build())
                .peek(AWSEfs -> log.debug("fileSystemPolicy => {}", AWSEfs.getFileSystemPolicy()))
                .collect(Collectors.toList());

    }

    private String getBackupPolicyStatus(EfsClient client, String fileSystemId) {
        try {
            return client.describeBackupPolicy(DescribeBackupPolicyRequest.builder()
                    .fileSystemId(fileSystemId)
                    .build()).backupPolicy().statusAsString();
        } catch (PolicyNotFoundException e) {
            // normal case
            return null;
        }
    }

    private String getFileSystemPolicy(EfsClient client, String fileSystemId) {
        try {
            return URLDecoder.decode(client.describeFileSystemPolicy(DescribeFileSystemPolicyRequest.builder()
                    .fileSystemId(fileSystemId)
                    .build()).policy(), StandardCharsets.UTF_8);
        } catch (PolicyNotFoundException e) {
            // normal case
            return null;
        }
    }

    private List<MountTargetDescription> getMountTargets(EfsClient client, String fileSystemId) {
        try {
            return client.describeMountTargets(DescribeMountTargetsRequest.builder()
                    .fileSystemId(fileSystemId)
                    .build()).mountTargets();
        } catch (MountTargetNotFoundException e) {
            // normal case
            return null;
        }
    }

    Maps<Resource> getResourceMaps(List<AWSEfs> awsEfses) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSEfs awsEfs : awsEfses) {
            FileSystemDescription fileSystem = awsEfs.getFileSystemDescription();

            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_efs_file_system")
                                    .name(getEfsFileSystemResourceName(fileSystem))
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
            if (awsEfs.getMountTargets() != null) {
                awsEfs.getMountTargets().forEach(mountTarget ->
                        resourceMapsBuilder.map(
                                        Resource.builder()
                                                .api(awsEfs.getTerraformResourceName())
                                                .name(getEfsMountTargetResourceName(mountTarget))
                                                .argument("file_system_id", TFExpression.builder()
                                                        .expression(MessageFormat.format("aws_efs_file_system.{0}.id",
                                                                getEfsFileSystemResourceName(fileSystem)))
                                                        .build())
                                                .argument("subnet_id", TFExpression.builder()
                                                        .expression(MessageFormat.format("aws_subnet.{0}.id", mountTarget.subnetId()))
                                                        .build())
                                                .build())
                                .build()
                );

            }
            if (awsEfs.getFileSystemPolicy() != null) {
                resourceMapsBuilder.map(
                                Resource.builder()
                                        .api("aws_efs_file_system_policy")
                                        .name(getEfsFileSystemPolicyResourceName(fileSystem))
                                        .argument("file_system_id", TFExpression.builder()
                                                .expression(MessageFormat.format("aws_efs_file_system.{0}.id",
                                                        getEfsFileSystemResourceName(fileSystem)))
                                                .build())
                                        .argument("policy", TFString.builder().isMultiline(true).value(
                                                        JsonUtils.toPrettyFormat(awsEfs.getFileSystemPolicy()))
                                                .build())
                                        .build())
                        .build();
            }

            if (awsEfs.getBackupPolicyStatus() != null) {
                resourceMapsBuilder.map(
                                Resource.builder()
                                        .api("aws_efs_backup_policy")
                                        .name(getEfsBackupPolicyResourceName(fileSystem))
                                        .argument("file_system_id", TFExpression.builder()
                                                .expression(MessageFormat.format("aws_efs_file_system.{0}.id",
                                                        getEfsFileSystemResourceName(fileSystem)))
                                                .build())
                                        .argument("backup_policy", TFMap.builder()
                                                .map("status", TFString.build(awsEfs.getBackupPolicyStatus()))
                                                .build())
                                        .build())
                        .build();
            }
        }
        return resourceMapsBuilder.build();
    }

    private String getEfsMountTargetResourceName(MountTargetDescription mountTarget) {
        return mountTarget.mountTargetId();
    }

    private String getEfsBackupPolicyResourceName(FileSystemDescription fileSystem) {
        return getEfsFileSystemResourceName(fileSystem);
    }

    private String getEfsFileSystemPolicyResourceName(FileSystemDescription fileSystem) {
        return getEfsFileSystemResourceName(fileSystem) + "-policy";
    }

    private String getEfsFileSystemResourceName(FileSystemDescription fileSystem) {
        return fileSystem.tags().stream()
                .filter(tag -> tag.key().equals("Name"))
                .findFirst()
                .map(Tag::value).orElse(fileSystem.fileSystemId());
    }

    TFImport getTFImport(List<AWSEfs> awsEfses) {
        TFImport.TFImportBuilder tfImportBuilder = TFImport.builder();
        awsEfses.forEach(awsEfs -> {
            tfImportBuilder.importLine(TFImportLine.builder()
                    .address(awsEfs.getTerraformAddress())
                    .id(awsEfs.getResourceId())
                    .build());

            if (awsEfs.getMountTargets() != null) {
                awsEfs.getMountTargets().forEach(mountTarget -> tfImportBuilder.importLine(TFImportLine.builder()
                        .address(MessageFormat.format("{0}.{1}",
                                "aws_efs_mount_target",
                                getEfsMountTargetResourceName(mountTarget)))
                        .id(mountTarget.mountTargetId())
                        .build()));
            }

            if (awsEfs.getFileSystemPolicy() != null) {
                tfImportBuilder.importLine(TFImportLine.builder()
                        .address(MessageFormat.format("{0}.{1}",
                                "aws_efs_file_system_policy",
                                getEfsFileSystemPolicyResourceName(awsEfs.getFileSystemDescription())))
                        .id(awsEfs.getFileSystemDescription().fileSystemId())
                        .build());
            }

            if (awsEfs.getBackupPolicyStatus() != null) {
                tfImportBuilder.importLine(TFImportLine.builder()
                        .address(MessageFormat.format("{0}.{1}",
                                "aws_efs_backup_policy",
                                getEfsBackupPolicyResourceName(awsEfs.getFileSystemDescription())))
                        .id(awsEfs.getFileSystemDescription().fileSystemId())
                        .build());
            }

        });
        return tfImportBuilder.build();
    }
}
