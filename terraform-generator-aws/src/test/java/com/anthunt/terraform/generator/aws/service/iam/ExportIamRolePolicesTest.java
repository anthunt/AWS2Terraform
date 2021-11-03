package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.iam.model.AWSRolePolicy;
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
import software.amazon.awssdk.services.iam.model.GetRolePolicyResponse;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportIamRolePolicesTest {

    private static ExportIamRolePolicies exportIamRolePolices;

    @Autowired
    private ResourceLoader resourceLoader;

    private static IamClient client;

    @BeforeAll
    public static void beforeAll() {
        exportIamRolePolices = new ExportIamRolePolicies();
        exportIamRolePolices.setDelayBetweenApis(0);
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AWS_GLOBAL).build();
        client = amazonClients.getIamClient();
    }

    private List<AWSRolePolicy> getAwsRolePolicies() {
        return List.of(
                AWSRolePolicy.builder()
                        .rolePolicy(GetRolePolicyResponse.builder()
                                .roleName("role-packer-base")
                                .policyName("policy-eks-describe")
                                .policyDocument(URLEncoder.encode(TestDataFileUtils.asString(
                                                resourceLoader.getResource("testData/aws/input/IamRolePolicyDocument.json")),
                                        StandardCharsets.UTF_8))
                                .build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getRolePolices() {

        List<AWSRolePolicy> awsRolePolicies = exportIamRolePolices.listRolePolices(client);

        log.debug("roles => {}", awsRolePolicies);
        awsRolePolicies.stream()
                .map(awsRolePolicy -> {
                    GetRolePolicyResponse rolePolicy = awsRolePolicy.getRolePolicy();
                    return GetRolePolicyResponse.builder()
                            .policyName(rolePolicy.policyName())
                            .roleName(rolePolicy.roleName())
                            .policyDocument(JsonUtils.toPrettyFormat(
                                    URLDecoder.decode(rolePolicy.policyDocument(), StandardCharsets.UTF_8)))
                            .build();
                })
                .forEach(role -> log.debug("role => {}", role.toString()));
    }

    @Test
    public void getResourceMaps() {
        Maps<Resource> resourceMaps = exportIamRolePolices.getResourceMaps(getAwsRolePolicies());
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/IamRolePolicy.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/IamRolePolicy.cmd"));
        String actual = exportIamRolePolices.getTFImport(getAwsRolePolicies()).script();

        assertEquals(expected, actual);
    }
}