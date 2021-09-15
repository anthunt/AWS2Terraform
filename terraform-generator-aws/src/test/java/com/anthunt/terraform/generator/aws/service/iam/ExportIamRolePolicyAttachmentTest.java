package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.iam.model.RolePolicyAttachmentDto;
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
class ExportIamRolePolicyAttachmentTest {

    private static ExportIamRolePolicyAttachment exportIamRolePolicyAttachment;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportIamRolePolicyAttachment = new ExportIamRolePolicyAttachment();
    }

    @Test
    public void getResourceMaps() {
        List<RolePolicyAttachmentDto> rolePolicyAttachmentDtos = List.of(
                RolePolicyAttachmentDto.builder()
                        .roleName("role-packer-base")
                        .policyName("policy-eks-describe")
                        .build(),
                RolePolicyAttachmentDto.builder()
                        .roleName("role-packer-base1")
                        .policyName("policy-eks-describe1")
                        .build()
        );
        Maps<Resource> resourceMaps = exportIamRolePolicyAttachment.getResourceMaps(rolePolicyAttachmentDtos);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/iam/expected/IamRolePolicyAttachment.tf")
        );
        assertEquals(expected, actual);
    }

}