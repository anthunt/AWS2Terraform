package com.anthunt.terraform.generator.aws;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.core.model.terraform.Terraform;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Provider;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import com.anthunt.terraform.generator.core.model.terraform.types.ProviderType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ActiveProfiles("custom")
@EntityScan("com.anthunt.terraform.generator.*")
@SpringBootTest(classes = {AmazonClients.class})
@SpringBootApplication
public class TerraformTest {

    @Autowired
    private AmazonClients amazonClients;

    public void contextLoads() {}

    public static String getTag(List<Tag> tags, String keyName) {
        return tags.stream().filter(tag->keyName.equals(tag.key())).findFirst().get().value();
    }

    @Test
    public void aws_ec2_to_terraform_string() {

        Maps<Provider> providerMapsBuilder = Maps.<Provider>builder()
                .map(
                        Provider.builder()
                                .providerType(ProviderType.AWS)
                                .arguments(
                                        TFArguments.builder()
                                                .argument("region", TFString.build(amazonClients.getRegion().id()))
                                                .argument("profile", TFString.build(amazonClients.getProfileName()))
                                                .build()
                                )
                                .build()
                )
                .build();

        Ec2Client ec2Client = amazonClients.getEc2Client();

        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        int i = 0;
        DescribeInstancesResponse describeInstancesResponse = ec2Client.describeInstances();
        for(Reservation reservation : describeInstancesResponse.reservations()) {
            for(Instance instance : reservation.instances()) {

                DescribeInstanceAttributeResponse disableApiTerminationAttribute = ec2Client.describeInstanceAttribute(DescribeInstanceAttributeRequest.builder()
                        .instanceId(instance.instanceId())
                        .attribute(InstanceAttributeName.DISABLE_API_TERMINATION)
                        .build());

                DescribeInstanceAttributeResponse shutdownBehaviorAttribute = ec2Client.describeInstanceAttribute(DescribeInstanceAttributeRequest.builder()
                        .instanceId(instance.instanceId())
                        .attribute(InstanceAttributeName.INSTANCE_INITIATED_SHUTDOWN_BEHAVIOR)
                        .build());

                DescribeInstanceAttributeResponse userDataAttribute = ec2Client.describeInstanceAttribute(DescribeInstanceAttributeRequest.builder()
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

        Terraform provider = Terraform.builder()
                .providers(providerMapsBuilder)
                .build();

        Terraform instance = Terraform.builder()
                .resources(resourceMapsBuilder.build())
                .build();

        log.info("result=>'{}'", provider.unmarshall());
        log.info("result=>'{}'", instance.unmarshall());

    }

}
