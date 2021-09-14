package com.anthunt.terraform.generator.aws.service.efs;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.efs.dto.EfsDto;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
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

        List<EfsDto> fileSystems = getEfs(client);

        return getResourceMaps(fileSystems);

    }

    List<EfsDto> getEfs(EfsClient client) {
        DescribeFileSystemsResponse describeFileSystems = client.describeFileSystems();
        return describeFileSystems.fileSystems().stream()
//                .peek(fileSystem -> log.debug("fileSystem => {}", fileSystem))
                .map(fileSystem -> EfsDto.builder()
                        .fileSystemDescription(fileSystem)
                        .backupPolicyStatus(getBackupPolicyStatus(client, fileSystem.fileSystemId()))
                        .fileSystemPolicy(getFileSystemPolicy(client, fileSystem.fileSystemId()))
                        .mountTargets(getMountTargets(client, fileSystem.fileSystemId()))
                        .build())
                .peek(efsDto -> log.debug("fileSystemPolicy => {}", efsDto.getFileSystemPolicy()))
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

    Maps<Resource> getResourceMaps(List<EfsDto> efsDtos) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        int index = 0;
        for (EfsDto efsDto : efsDtos) {
            FileSystemDescription fileSystem = efsDto.getFileSystemDescription();
            String fileSystemName = fileSystem.tags().stream()
                    .filter(tag -> tag.key().equals("Name"))
                    .findFirst()
                    .map(tag -> tag.value()).orElse("aws_efs_file_system-" + index++);

            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_efs_file_system")
                                    .name(fileSystemName)
                                    .arguments(
                                            TFArguments.builder()
                                                    .argument("encrypted", TFBool.build(fileSystem.encrypted()))
                                                    .argument("kms_key_id", TFString.build(fileSystem.kmsKeyId()))
                                                    .argument("performance_mode", TFString.build(fileSystem.performanceModeAsString()))
                                                    .argument("throughput_mode", TFString.build(fileSystem.throughputModeAsString()))
                                                    .argument("provisioned_throughput_in_mibps",
                                                            Optional.ofNullable(fileSystem.provisionedThroughputInMibps())
                                                                    .map(v -> TFNumber.build(v.toString())).orElse(TFNumber.build(null)))
                                                    .argument("tags", TFMap.build(
                                                            fileSystem.tags().stream()
                                                                    .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                                    ))
                                                    .build())
                                    .build())
                    .build();
            if (efsDto.getMountTargets() != null) {
                efsDto.getMountTargets().stream().forEach(mountTarget ->
                        resourceMapsBuilder.map(
                                        Resource.builder()
                                                .api("aws_efs_mount_target")
                                                .name(mountTarget.mountTargetId())
                                                .arguments(TFArguments.builder()
                                                        .argument("file_system_id", TFExpression.builder()
                                                                .expression(MessageFormat.format("aws_efs_file_system.{0}.id", fileSystemName))
                                                                .build())
                                                        .argument("subnet_id", TFExpression.builder()
                                                                .expression(MessageFormat.format("aws_subnet.{0}.id", mountTarget.subnetId()))
                                                                .build())
                                                        .build())
                                                .build())
                                .build()
                );

            }
            if (efsDto.getFileSystemPolicy() != null) {
                resourceMapsBuilder.map(
                                Resource.builder()
                                        .api("aws_efs_file_system_policy")
                                        .name(fileSystemName + "-policy")
                                        .arguments(TFArguments.builder()
                                                .argument("file_system_id", TFExpression.builder()
                                                        .expression(MessageFormat.format("aws_efs_file_system.{0}.id", fileSystemName))
                                                        .build())
                                                .argument("policy", TFString.builder().isMultiline(true).value(
                                                                JsonUtils.toPrettyFormat(efsDto.getFileSystemPolicy()))
                                                        .build())
                                                .build())
                                        .build())
                        .build();
            }

            if (efsDto.getBackupPolicyStatus() != null) {
                resourceMapsBuilder.map(
                                Resource.builder()
                                        .api("aws_efs_backup_policy")
                                        .name(fileSystemName)
                                        .arguments(TFArguments.builder()
                                                .argument("file_system_id", TFExpression.builder()
                                                        .expression(MessageFormat.format("aws_efs_file_system.{0}.id", fileSystemName))
                                                        .build())
                                                .argument("backup_policy", TFMap.builder()
                                                        .map("status", TFString.build(efsDto.getBackupPolicyStatus()))
                                                        .build())
                                                .build())
                                        .build())
                        .build();
            }
        }
        return resourceMapsBuilder.build();
    }
}
