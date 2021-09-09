package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportSecurityGroupsTest {

    private static ExportSecurityGroups exportSecurityGroups;

    @BeforeAll
    public static void beforeAll() {
        exportSecurityGroups = new ExportSecurityGroups();
    }

    @Test
    void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();

        Maps<Resource> export = exportSecurityGroups.export(ec2Client, null, null);

        log.debug("result => {}", export.unmarshall());
    }

    @Test
    void getSecurityGroups() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();

        List<SecurityGroup> securityGroups = exportSecurityGroups.getSecurityGroups(ec2Client);
        log.debug("securityGroups => {}", securityGroups);
    }

    @Test
    void getResourceMaps() {
        // given
        List<SecurityGroup> securityGroups = new ArrayList<>();
        SecurityGroup securityGroup = SecurityGroup.builder()
                .description("test description")
                .groupName("sg_test")
                .groupId("sg-002efaf20710623d5")
                .ipPermissions(
                        IpPermission.builder()
                                .fromPort(3306)
                                .ipProtocol("tcp")
                                .toPort(3306)
                                .userIdGroupPairs(
                                        UserIdGroupPair.builder()
                                                .groupId("sg-002efaf20710623d5")
                                                .userId("400661667959")
                                                .build(),
                                        UserIdGroupPair.builder()
                                                .groupId("sg-05465424de0e9d80b")
                                                .userId("400661667959")
                                                .build(),
                                        UserIdGroupPair.builder()
                                                .description("ssh hub to aurora service")
                                                .groupId("sg-0575ae95fd6c58c75")
                                                .userId("400661667959")
                                                .build()
                                ).build()
                ).ipPermissionsEgress(
                        IpPermission.builder()
                                .ipProtocol("-1")
                                .ipRanges(IpRange.builder().cidrIp("0.0.0.0/0").build())
                                .ipv6Ranges(Ipv6Range.builder().cidrIpv6("::/0").build())
                                .build(),
                        IpPermission.builder()
                                .fromPort(3306)
                                .ipProtocol("tcp")
                                .toPort(3306)
                                .userIdGroupPairs(
                                        UserIdGroupPair.builder()
                                                .groupId("sg-002efaf20710623d5")
                                                .userId("400661667959")
                                                .build()
                                ).build()
                ).build();

        securityGroups.add(securityGroup);

        Maps<Resource> resourceMaps = exportSecurityGroups.getResourceMaps(securityGroups);
        log.debug("resourceMaps => \n{}", resourceMaps.unmarshall());
    }
}