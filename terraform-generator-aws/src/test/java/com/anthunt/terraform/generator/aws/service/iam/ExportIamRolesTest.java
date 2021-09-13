package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.support.DisabledOnNoAwsCredentials;
import com.anthunt.terraform.generator.aws.support.TestDataFileUtils;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
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
import software.amazon.awssdk.services.iam.model.Role;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportIamRolesTest {

    private static ExportIamRoles exportIamRoles;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportIamRoles = new ExportIamRoles();
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getRoles() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AWS_GLOBAL).build();
        IamClient client = amazonClients.getIamClient();

        List<Role> roles = exportIamRoles.getRoles(client);

        roles.stream()
                .map(role ->
                        Role.builder()
                                .arn(role.arn())
                                .arn(role.roleName())
                                .arn(role.description())
                                .arn(role.path())
                                .assumeRolePolicyDocument(JsonUtils.toPrettyFormat(
                                        URLDecoder.decode(role.assumeRolePolicyDocument(), StandardCharsets.UTF_8)))
                                .build())
                .forEach(role -> log.debug("role => {}", role.toString()));
    }

    @Test
    public void getResourceMaps() {
        List<Role> roles = List.of(
                Role.builder()
                        .path("/")
                        .roleName("testRole")
                        .description("test description")
                        .arn("arn:aws:iam::100000000000:role/AmazonEKS_EFS_CSI_DriverRole")
                        .assumeRolePolicyDocument(URLEncoder.encode(TestDataFileUtils.asString(
                                resourceLoader.getResource("testData/iam/input/IamRoleAssumeRolePolicyDocument.json")),
                                        StandardCharsets.UTF_8))
                        .build()

        );
        Maps<Resource> resourceMaps = exportIamRoles.getResourceMaps(roles);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/iam/expected/IamRole.tf")
        );
        assertEquals(expected, actual);
    }

}