package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSSecurityGroup;
import com.anthunt.terraform.generator.aws.support.DisabledOnNoAwsCredentials;
import com.anthunt.terraform.generator.aws.support.TestDataFileUtils;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportSecurityGroupsTest {

    private static ExportSecurityGroups exportSecurityGroups;

    @Autowired
    private ResourceLoader resourceLoader;

    private static Ec2Client client;

    @BeforeAll
    public static void beforeAll() {
        exportSecurityGroups = new ExportSecurityGroups();
        exportSecurityGroups.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getEc2Client();
    }

    private List<AWSSecurityGroup> getAwsSecurityGroups() {
        return List.of(
                AWSSecurityGroup.builder()
                        .securityGroup(SecurityGroup.builder()
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
                                ).build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {

        Maps<Resource> export = exportSecurityGroups.export(client, null, null);

        log.debug("result => {}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    void listSecurityGroups() {
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        Ec2Client ec2Client = AmazonClients.getEc2Client();

        List<AWSSecurityGroup> securityGroups = exportSecurityGroups.listAwsSecurityGroups(ec2Client);
        log.debug("securityGroups => {}", securityGroups);
    }

    @Test
    void getResourceMaps() {
        // given
        List<AWSSecurityGroup> awsSecurityGroups = getAwsSecurityGroups();

        Maps<Resource> resourceMaps = exportSecurityGroups.getResourceMaps(awsSecurityGroups);
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/SecurityGroup.tf"));
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/SecurityGroup.cmd"));
        String actual = exportSecurityGroups.getTFImport(getAwsSecurityGroups()).script();

        assertEquals(expected, actual);
    }
}