package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.iam.model.AWSPolicy;
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
import software.amazon.awssdk.services.iam.model.Policy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportIamPoliciesTest {
    private static ExportIamPolicies exportIamPolicies;

    @Autowired
    private ResourceLoader resourceLoader;

    private static IamClient client;

    @BeforeAll
    public static void beforeAll() {
        exportIamPolicies = new ExportIamPolicies();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AWS_GLOBAL).build();
        client = amazonClients.getIamClient();
    }

    private List<AWSPolicy> getAwsPolicies() {
        return List.of(
                AWSPolicy.builder().policy(
                        Policy.builder()
                                .policyName("AWSLoadBalancerControllerIAMPolicy")
                                .policyId("ANPATNPDYKVFHJVH2URK4")
                                .arn("arn:aws:iam::235090236746:policy/AWSLoadBalancerControllerIAMPolicy")
                                .path("/")
                                .description("test")
                                .build()
                ).document(TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/input/IamPolicyDocument.json"))
                ).build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void getPolices() {
        List<AWSPolicy> polices = exportIamPolicies.listAwsPolices(client);
        log.debug("polices => {}", polices);
    }

    @Test
    public void getResourceMaps() {
        //given
        List<AWSPolicy> awsPolicy = getAwsPolicies();

        Maps<Resource> resourceMaps = exportIamPolicies.getResourceMaps(awsPolicy);

        String actual = resourceMaps.unmarshall();
        log.debug("actual => \n{}", actual);

        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/IamPolicy.tf"));
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/IamPolicy.cmd"));
        String actual = exportIamPolicies.getTFImport(getAwsPolicies()).script();

        assertEquals(expected, actual);
    }
}