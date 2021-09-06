package com.anthunt.terraform.generator.aws.service.ec2;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.ec2.dto.CustomDescribeInstancesResponse;
import com.anthunt.terraform.generator.aws.service.ec2.dto.CustomInstance;
import com.anthunt.terraform.generator.aws.service.ec2.dto.CustomReservation;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportInstances extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        CustomDescribeInstancesResponse customDescribeInstancesResponse = getDescribeInstancesResponse(client);

        return getResourceMaps(customDescribeInstancesResponse);

    }

    protected CustomDescribeInstancesResponse getDescribeInstancesResponse(Ec2Client client) {

        CustomDescribeInstancesResponse customDescribeInstancesResponse = new CustomDescribeInstancesResponse();
        DescribeInstancesResponse describeInstancesResponse = client.describeInstances();
        List<CustomReservation> customReservations = new ArrayList<CustomReservation>();
        customDescribeInstancesResponse.setReservations(customReservations);

        for(Reservation reservation : describeInstancesResponse.reservations()) {
            CustomReservation customReservation = new CustomReservation();
            customReservations.add(customReservation);

            for (Instance instance : reservation.instances()) {
                CustomInstance customInstance = new CustomInstance();
                customInstance.setInstance(instance);
                customReservation.add(customInstance);

                DescribeInstanceAttributeResponse disableApiTerminationAttribute = client.describeInstanceAttribute(DescribeInstanceAttributeRequest.builder()
                        .instanceId(instance.instanceId())
                        .attribute(InstanceAttributeName.DISABLE_API_TERMINATION)
                        .build());
                customInstance.setDisableApiTermination(disableApiTerminationAttribute.disableApiTermination().value());

                DescribeInstanceAttributeResponse shutdownBehaviorAttribute = client.describeInstanceAttribute(DescribeInstanceAttributeRequest.builder()
                        .instanceId(instance.instanceId())
                        .attribute(InstanceAttributeName.INSTANCE_INITIATED_SHUTDOWN_BEHAVIOR)
                        .build());
                customInstance.setShutdownBehavior(shutdownBehaviorAttribute.instanceInitiatedShutdownBehavior().value());

                DescribeInstanceAttributeResponse userDataAttribute = client.describeInstanceAttribute(DescribeInstanceAttributeRequest.builder()
                        .instanceId(instance.instanceId())
                        .attribute(InstanceAttributeName.USER_DATA)
                        .build());
                customInstance.setUserData(userDataAttribute.userData().value());

            }
        }

        return customDescribeInstancesResponse;
    }

    private Maps<Resource> getResourceMaps(CustomDescribeInstancesResponse describeInstancesResponse) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        int i = 0;
        for(CustomReservation reservation : describeInstancesResponse.getReservations()) {
            for(CustomInstance customInstance : reservation.getInstances()) {
                Instance instance = customInstance.getInstance();
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
                                                .argument("disable_api_termination", TFBool.build(customInstance.getDisableApiTermination()))
                                                .argument("instance_initiated_shutdown_behavior", TFString.build(customInstance.getShutdownBehavior()))
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
                                                        customInstance.getUserData() != null ?
                                                                new String(Base64.getDecoder().decode(customInstance.getUserData())).replaceAll("[$]", "\\$\\$")
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

        return resourceMapsBuilder.build();
    }

}
