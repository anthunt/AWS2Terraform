package com.anthunt.terraform.generator.aws.service.ec2;

import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.core.model.terraform.Terraform;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportInstances extends AbstractExport<Ec2Client> {

    public static String getTag(List<Tag> tags, String keyName) {
        return tags.stream().filter(tag->keyName.equals(tag.key())).findFirst().get().value();
    }

    @Override
    protected void export(Ec2Client client) {

        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        int i = 0;
        DescribeInstancesResponse describeInstancesResponse = client.describeInstances();
        for(Reservation reservation : describeInstancesResponse.reservations()) {
            for(Instance instance : reservation.instances()) {

                DescribeInstanceAttributeResponse disableApiTerminationAttribute = client.describeInstanceAttribute(DescribeInstanceAttributeRequest.builder()
                        .instanceId(instance.instanceId())
                        .attribute(InstanceAttributeName.DISABLE_API_TERMINATION)
                        .build());

                DescribeInstanceAttributeResponse shutdownBehaviorAttribute = client.describeInstanceAttribute(DescribeInstanceAttributeRequest.builder()
                        .instanceId(instance.instanceId())
                        .attribute(InstanceAttributeName.INSTANCE_INITIATED_SHUTDOWN_BEHAVIOR)
                        .build());

                DescribeInstanceAttributeResponse userDataAttribute = client.describeInstanceAttribute(DescribeInstanceAttributeRequest.builder()
                        .instanceId(instance.instanceId())
                        .attribute(InstanceAttributeName.USER_DATA)
                        .build());

                resourceMapsBuilder.map(
                        Resource.builder()
                                .api("aws_instance")
                                .name("instance" + i)
                                .arguments(
                                        TFArguments.builder()
                                                .argument("ami", TFString.build(instance.imageId()))
                                                .argument("placement_group", TFString.build(instance.placement().groupName()))
                                                .argument("tenancy", TFString.build(instance.placement().tenancyAsString()))
                                                .argument("host_id", TFString.build(instance.placement().hostId()))
                                                .argument("cpu_core_count", TFNumber.build(instance.cpuOptions().coreCount().toString()))
                                                .argument("cpu_threads_per_core", TFNumber.build(instance.cpuOptions().threadsPerCore().toString()))
                                                .argument("ebs_optimized", TFBool.build(instance.ebsOptimized()))
                                                .argument("disable_api_termination", TFBool.build(disableApiTerminationAttribute.disableApiTermination().value()))
                                                .argument("instance_initiated_shutdown_behavior", TFString.build(shutdownBehaviorAttribute.instanceInitiatedShutdownBehavior().value()))
                                                .argument("instance_type", TFString.build(instance.instanceType().toString()))
                                                .argument("key_name", TFString.build(instance.keyName()))
                                                //.argument("get_password_data", TFBool.build(instance.pass))
                                                .argument("monitoring", TFBool.build(instance.monitoring().state() == MonitoringState.ENABLED))
                                                .argument("vpc_security_group_ids", TFList.builder()
                                                        .lists(instance.securityGroups().stream().map(sg->TFString.build(sg.groupId())).collect(Collectors.toList()))
                                                        .build())
                                                .argument("subnet_id", TFString.build(instance.subnetId()))
                                                //.argument("associate_public_ip_address", TFBool.build(instance.))
                                                .argument("private_ip", TFString.build(instance.privateIpAddress()))
                                                .argument("secondary_private_ips", TFList.builder()
                                                        .lists(
                                                                instance.networkInterfaces().stream()
                                                                        .flatMap(
                                                                                ni->ni.privateIpAddresses().stream()
                                                                                        .filter(nis -> !nis.primary())
                                                                                        .map(nis->TFString.build(nis.privateIpAddress()))
                                                                        )
                                                                        .collect(Collectors.toList())
                                                        )
                                                        .build())
                                                .argument("source_dest_check", TFBool.build(instance.sourceDestCheck()))
                                                .argument("user_data", TFString.builder().isMultiline(true).value(
                                                        userDataAttribute.userData().value() != null ?
                                                                new String(Base64.getDecoder().decode(userDataAttribute.userData().value())).replaceAll("[$]", "\\$\\$")
                                                                : ""
                                                ).build())
                                                .argument("iam_instance_profile", TFString.build(
                                                        instance.iamInstanceProfile() == null ? "" : instance.iamInstanceProfile().arn()
                                                ))
                                                //.argument("ipv6_address_count", TFNumber.build(instance.))
                                                //.argument("ipv6_address")
                                                .argument("tags", TFMap.build(
                                                        instance.tags().stream()
                                                                .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                                                ))
                                                /*
                                                .argument("root_block_device",
                                                        TFBlock.builder()
                                                                .arguments(
                                                                        TFArguments.builder()
                                                                                .argument("volume_type", instance.blockDeviceMappings().get(0).)
                                                                                .argument("volume_size", null)
                                                                                .argument("iops", null)
                                                                                .argument("delete_on_termination", null)
                                                                                .argument("encrypted", null)
                                                                                .argument("kms_key_id", null)
                                                                                .build()
                                                                )
                                                        .build()
                                                )
                                                */
                                                //.argument("ebs_block_device", )
                                                //.argument("ephemeral_block_device", )
                                                //.argument("network_interface", )
                                                //.argument("credit_specification", )
                                                .argument("hibernation", TFBool.build(instance.hibernationOptions().configured()))
                                                .argument("metadata_options",
                                                        TFBlock.builder()
                                                                .arguments(
                                                                        TFArguments.builder()
                                                                                .argument("http_endpoint", TFString.build(instance.metadataOptions().httpEndpointAsString()))
                                                                                .argument("http_tokens", TFString.build(instance.metadataOptions().httpTokensAsString()))
                                                                                .argument("http_put_response_hop_limit", TFNumber.build(instance.metadataOptions().httpPutResponseHopLimit().toString()))
                                                                                .build()
                                                                )
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                );
                i++;
            }
        }

        Terraform instance = Terraform.builder()
                .resources(resourceMapsBuilder.build())
                .build();

        log.info("result=>'{}'", instance.unmarshall());

    }

}
