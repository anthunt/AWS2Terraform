package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.iam.dto.PolicyDto;
import com.anthunt.terraform.generator.aws.support.TestDataFileUtils;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.Policy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportIamPoliciesTest {
    private static ExportIamPolicies exportIamPolicies;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportIamPolicies = new ExportIamPolicies();
    }

    @Test
    public void getPolices() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("ulsp-dev").region(Region.AWS_GLOBAL).build();
        IamClient client = amazonClients.getIamClient();

        List<PolicyDto> polices = exportIamPolicies.getPolices(client);
        log.debug("polices => {}", polices);
    }

    @Test
    public void getResourceMaps() throws IOException {
        //given
        List<PolicyDto> policeDtos = List.of(
                PolicyDto.builder().policy(
                    Policy.builder()
                    .policyName("AWSLoadBalancerControllerIAMPolicy")
                            .policyId("ANPATNPDYKVFHJVH2URK4")
                            .arn("arn:aws:iam::235090236746:policy/AWSLoadBalancerControllerIAMPolicy")
                            .path("/")
                            .description("test")
                    .build()
                ).document(TestDataFileUtils.asString(resourceLoader.getResource("testData/iam/input/IamPolicyDocument.json"))
                ).build()
        );

        Maps<Resource> resourceMaps = exportIamPolicies.getResourceMaps(policeDtos);

        String actual = resourceMaps.unmarshall();
        log.debug("actual => \n{}", actual);

        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/iam/expected/IamPolicy.tf"));
        assertEquals(expected, actual);
    }
}