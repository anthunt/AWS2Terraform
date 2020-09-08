package com.anthunt.terraform.generator.core.model;

import com.anthunt.terraform.generator.core.model.terraform.Terraform;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.types.ProviderType;
import com.anthunt.terraform.generator.core.model.terraform.nodes.*;
import com.anthunt.terraform.generator.core.model.terraform.types.VariableType;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class Test {

    public static String getTag(List<Tag> tags, String keyName) {
        return tags.stream().filter(tag->keyName.equals(tag.key())).findFirst().get().value();
    }

    public static void main(String[] args) {

        Ec2Client ec2Client = Ec2Client.builder().credentialsProvider(ProfileCredentialsProvider.create("SFA-DEV")).region(Region.AP_NORTHEAST_2).build();

        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.<Resource>builder();

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


                //instance.tags().

                resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_instance")
                            .name(getTag(instance.tags(), "Name"))
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
                                            .argument("vpc_security_groups", TFList.builder()
                                                    .lists(instance.securityGroups().stream().map(sg->TFString.build(sg.groupId())).collect(Collectors.toList()))
                                                    .build())
                                            .argument("subnet_id", TFString.build(instance.subnetId()))
                                            //.argument("associate_public_ip_address", TFBool.build(instance.))
                                            .argument("private_ip", TFString.build(instance.privateIpAddress()))
                                            /*
                                            .argument("secondary_private_ips", TFList.builder()
                                                    .lists(instance.networkInterfaces().)
                                                    .build())
                                             */
                                            .argument("source_dest_check", TFBool.build(instance.sourceDestCheck()))
                                            .argument("user_data_base64", TFString.build(userDataAttribute.userData().value()))
                                            .argument("iam_instance_profile", TFString.build(instance.iamInstanceProfile().arn()))
                                            //.argument("ipv6_address_count", TFNumber.build(instance.))
                                            //.argument("ipv6_address")
                                            //.argument("tags", TFMap.builder().maps().build())
                                            .build()
                            )
                            .build()
                );
            }
        }

        Terraform terraform = Terraform.builder()
                .resources(resourceMapsBuilder.build())
                .build();

        /*
        Terraform terraform = Terraform.builder()
                .variables(
                        Maps.<Variable>builder()
                                .map(Variable.builder()
                                        .name("testValue")
                                        .defaultValue(TFString.builder().value("value").build())
                                        .build())
                                .build()
                )
                .locals(Locals.builder()
                        .local("testKey", TFString.builder().value("testValue").build())
                        .local("testKey2", TFBool.builder().bool(true).build())
                        .local("testKey3", TFNumber.builder().value("1").build())
                        .local("testKey4", TFObject.builder().member("member1", TFString.builder().value("member-value").build()).build())
                        .local("testKey5", TFList.builder().list(TFString.builder().value("list1").build()).build())
                        .local("testKey6",
                                TFMap.builder()
                                        .map("key1", TFString.builder().value("val1").build())
                                        .map("key2", TFList.builder().list(TFString.builder().value("member1").build()).build())
                                .build())
                        .build())
                .providers(
                        Maps.<Provider>builder()
                                .map(Provider.builder()
                                        .providerType(ProviderType.AWS)
                                        .arguments(TFArguments.builder()
                                                .argument("alias", TFString.builder().value("dev").build())
                                                .argument("region", TFString.builder().value("ap-northeast-2").build())
                                                .argument("profile", TFString.builder().value("HGI-LOG").build())
                                                .build())
                                        .build())
                            .build()
                )
                .resources(
                        Maps.<Resource>builder()
                                .map(Resource.builder()
                                        .api("aws_iam_role")
                                        .name("testRole")
                                        .arguments(TFArguments.builder()
                                                .argument("provider", TFExpression.builder().expression("aws.dev").build())
                                                .argument("argument1", TFString.builder().value("value1").build())
                                                .argument("lifecycle", TFBlock.builder().arguments(
                                                        TFArguments.builder()
                                                                .argument("create", TFString.builder().value("30m").build())
                                                                .argument("delete", TFString.builder().value("30m").build())
                                                                .build()
                                                    ).build())
                                                .build())
                                        .build())
                                .build()
                )
                .build();
        */
        System.out.println(terraform.unmarshall());
    }

}
