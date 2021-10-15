package com.anthunt.terraform.generator.aws.service.ec2;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeLaunchTemplatesResponse;
import software.amazon.awssdk.services.ec2.model.LaunchTemplateVersion;
import software.amazon.awssdk.services.ec2.model.ResponseLaunchTemplateData;

import java.text.MessageFormat;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportlaunchTemplates extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<LaunchTemplateVersion> launchTemplateVersions = listLaunchTemplateVersions(client);

        return getResourceMaps(launchTemplateVersions);

    }

    List<LaunchTemplateVersion> listLaunchTemplateVersions(Ec2Client client) {

        DescribeLaunchTemplatesResponse describeLaunchTemplatesResponse = client.describeLaunchTemplates();

        return describeLaunchTemplatesResponse.launchTemplates().stream()
                .map(launchTemplate -> client.describeLaunchTemplateVersions(builder -> builder
                                .launchTemplateId(launchTemplate.launchTemplateId())
                                .versions(launchTemplate.latestVersionNumber().toString()).build())
                        .launchTemplateVersions().stream().findFirst().get())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<LaunchTemplateVersion> launchTemplateVersions) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        for (LaunchTemplateVersion version : launchTemplateVersions) {
            ResponseLaunchTemplateData data = version.launchTemplateData();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_launch_template")
                            .name(version.launchTemplateName())
                            .argument("name", TFString.build(version.launchTemplateName()))
                            .argument("disable_api_termination", TFBool.build(data.disableApiTermination()))
                            .argument("ebs_optimized", TFBool.build(data.ebsOptimized()))
                            .argument("image_id", TFString.build(data.imageId()))
                            .argument("instance_initiated_shutdown_behavior",
                                    TFString.build(data.instanceInitiatedShutdownBehaviorAsString()))
                            .argument("instance_type", TFString.build(data.instanceTypeAsString()))
                            .argument("kernel_id", TFString.build(data.kernelId()))
                            .argument("key_name", TFString.build(data.keyName()))
                            .argument("ram_disk_id", TFString.build(data.ramDiskId()))
                            .argument("vpc_security_group_ids",
                                    TFList.build(data.securityGroupIds().stream()
                                            .map(id -> TFString.build(id))
                                            .collect(Collectors.toList())))
                            .argumentsIf(Optional.ofNullable(data.blockDeviceMappings()).isPresent(),
                                    "block_device_mappings",
                                    data.blockDeviceMappings().stream()
                                            .map(blockDevice -> TFBlock.builder()
                                                    .argument("device_name",
                                                            TFString.build(blockDevice.deviceName()))
                                                    .argumentIf(Optional.ofNullable(blockDevice.ebs()).isPresent(),
                                                            "ebs",
                                                            () -> TFBlock.builder()
                                                                    .argument("volume_size",
                                                                            TFNumber.build(blockDevice.ebs().volumeSize()))
                                                                    .build())
                                                    .build())
                                            .collect(Collectors.toList()))
                            .argumentIf(Optional.ofNullable(data.capacityReservationSpecification()).isPresent(),
                                    "capacity_reservation_specification",
                                    () -> TFBlock.builder()
                                            .argument("capacity_reservation_preference",
                                                    TFString.build(data.capacityReservationSpecification()
                                                            .capacityReservationPreferenceAsString()))
                                            .build())
                            .argumentIf(Optional.ofNullable(data.cpuOptions()).isPresent(),
                                    "cpu_options",
                                    () -> TFBlock.builder()
                                            .argument("core_count",
                                                    TFNumber.build(data.cpuOptions().coreCount()))
                                            .argument("threads_per_core",
                                                    TFNumber.build(data.cpuOptions().threadsPerCore()))
                                            .build())
                            .argumentIf(Optional.ofNullable(data.creditSpecification()).isPresent(),
                                    "credit_specification",
                                    () -> TFBlock.builder()
                                            .argument("cpu_credits",
                                                    TFString.build(data.creditSpecification().cpuCredits()))
                                            .build())
                            .argumentIf(Optional.ofNullable(data.elasticGpuSpecifications()).isPresent(),
                                    "elastic_gpu_specifications",
                                    () -> TFBlock.builder()
                                            .argument("type ",
                                                    TFString.build(data.elasticGpuSpecifications().stream()
                                                            .findFirst()
                                                            .map(r -> r.type())
                                                            .orElse(null)))
                                            .build())
                            .argumentIf(Optional.ofNullable(data.elasticInferenceAccelerators()).isPresent(),
                                    "elastic_inference_accelerator",
                                    () -> TFBlock.builder()
                                            .argument("type ",
                                                    TFString.build(data.elasticInferenceAccelerators().stream()
                                                            .findFirst()
                                                            .map(r -> r.type())
                                                            .orElse(null)))
                                            .build())
                            .argumentIf(Optional.ofNullable(data.iamInstanceProfile()).isPresent(),
                                    "iam_instance_profile",
                                    () -> TFBlock.builder()
                                            .argument("name ",
                                                    TFString.build(data.iamInstanceProfile().name()))
                                            .build())
                            .argumentIf(Optional.ofNullable(data.instanceMarketOptions()).isPresent(),
                                    "instance_market_options",
                                    () -> TFBlock.builder()
                                            .argument("market_type",
                                                    TFString.build(data.instanceMarketOptions().marketTypeAsString()))
                                            .build())
                            .argumentIf(Optional.ofNullable(data.licenseSpecifications()).isPresent(),
                                    "license_specification",
                                    () -> TFBlock.builder()
                                            .argument("market_type",
                                                    TFString.build(data.licenseSpecifications().stream()
                                                            .findFirst()
                                                            .map(r -> r.licenseConfigurationArn())
                                                            .orElse(null)))
                                            .build())
                            .argumentIf(Optional.ofNullable(data.metadataOptions()).isPresent(),
                                    "metadata_options",
                                    () -> TFBlock.builder()
                                            .argument("http_endpoint ",
                                                    TFString.build(data.metadataOptions().httpEndpointAsString()))
                                            .argument("http_tokens",
                                                    TFString.build(data.metadataOptions().httpTokensAsString()))
                                            .argument("http_put_response_hop_limit",
                                                    TFNumber.build(data.metadataOptions().httpPutResponseHopLimit()))
                                            .build())
                            .argumentIf(Optional.ofNullable(data.monitoring()).isPresent(),
                                    "monitoring",
                                    () -> TFBlock.builder()
                                            .argument("cpu_credits",
                                                    TFBool.build(data.monitoring().enabled()))
                                            .build())
                            .argumentsIf(Optional.ofNullable(data.networkInterfaces()).isPresent(),
                                    "network_interfaces",
                                    () -> data.networkInterfaces().stream()
                                            .map(r -> TFBlock.builder()
                                                    .argument("associate_public_ip_address",
                                                            TFBool.build(r.associatePublicIpAddress()))
                                                    .build())
                                            .collect(Collectors.toList()))
                            .argumentIf(Optional.ofNullable(data.placement()).isPresent(),
                                    "placement",
                                    () -> TFBlock.builder()
                                            .argument("availability_zone",
                                                    TFString.build(data.placement().availabilityZone()))
                                            .build())
                            .argument("user_data", TFExpression.builder()
                                    .expression(data.userData() != null ?
                                            MessageFormat.format("base64encode(\n<<EOF\n{0}\nEOF\n)",
                                                    new String(Base64.getDecoder().decode(data.userData()))
                                                            .replaceAll("[$]", "\\$\\$"))
                                            : "")
                                    .build())
                            .build());
        }

        return resourceMapsBuilder.build();
    }

}
