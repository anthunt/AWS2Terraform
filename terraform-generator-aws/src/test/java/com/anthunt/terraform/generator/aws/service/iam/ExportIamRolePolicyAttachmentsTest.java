package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.iam.model.AWSRolePolicyAttachment;
import com.anthunt.terraform.generator.aws.support.TestDataFileUtils;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportIamRolePolicyAttachmentsTest {

    private static ExportIamRolePolicyAttachments exportIamRolePolicyAttachments;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportIamRolePolicyAttachments = new ExportIamRolePolicyAttachments();
        exportIamRolePolicyAttachments.setDelayBetweenApis(0);
    }

    private List<AWSRolePolicyAttachment> getAwsRolePolicyAttachments() {
        return List.of(
                AWSRolePolicyAttachment.builder()
                        .roleName("role-packer-base")
                        .policyName("policy-eks-describe")
                        .build(),
                AWSRolePolicyAttachment.builder()
                        .roleName("role-packer-base1")
                        .policyName("policy-eks-describe1")
                        .build()
        );
    }

    @Test
    public void getResourceMaps() {
        List<AWSRolePolicyAttachment> awsRolePolicyAttachments = getAwsRolePolicyAttachments();
        Maps<Resource> resourceMaps = exportIamRolePolicyAttachments.getResourceMaps(awsRolePolicyAttachments);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/IamRolePolicyAttachment.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/IamRolePolicyAttachment.cmd"));
        String actual = exportIamRolePolicyAttachments.getTFImport(getAwsRolePolicyAttachments()).script();

        assertEquals(expected, actual);
    }
}