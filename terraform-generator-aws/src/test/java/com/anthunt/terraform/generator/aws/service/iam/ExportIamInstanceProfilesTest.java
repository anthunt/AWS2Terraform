package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.iam.model.AWSInstanceProfile;
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
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.InstanceProfile;
import software.amazon.awssdk.services.iam.model.Role;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportIamInstanceProfilesTest {

    private static ExportIamInstanceProfiles exportIamInstanceProfiles;

    @Autowired
    private ResourceLoader resourceLoader;

    private static IamClient client;

    @BeforeAll
    public static void beforeAll() {
        exportIamInstanceProfiles = new ExportIamInstanceProfiles();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AWS_GLOBAL).build();
        client = amazonClients.getIamClient();
    }

    private List<AWSInstanceProfile> getInstanceProfiles() {
        return List.of(
                AWSInstanceProfile.builder()
                        .instanceProfile(InstanceProfile.builder()
                                .instanceProfileName("eks-7cbddf86-c0a6-643b-dbdd-85b97c390535")
                                .roles(Role.builder().roleName("eks-cluster-workernode-role").build())
                                .build())
                        .build(),
                AWSInstanceProfile.builder()
                        .instanceProfile(InstanceProfile.builder()
                                .instanceProfileName("role-packer-base")
                                .roles(Role.builder().roleName("role-packer-base").build(),
                                        Role.builder().roleName("role-packer-base2").build())
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportIamInstanceProfiles.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        List<AWSInstanceProfile> instanceProfiles = getInstanceProfiles();
        Maps<Resource> resourceMaps = exportIamInstanceProfiles.getResourceMaps(instanceProfiles);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/IamInstanceProfile.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/IamInstanceProfile.cmd"));
        String actual = exportIamInstanceProfiles.getTFImport(getInstanceProfiles()).script();

        assertEquals(expected, actual);
    }
}