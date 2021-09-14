package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.vpc.dto.VpcDto;
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
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.Vpc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportVpcsTest {

    private static ExportVpcs exportvpcs;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportvpcs = new ExportVpcs();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();

        Maps<Resource> export = exportvpcs.export(ec2Client, null, null);

        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    void getResourceMaps() {
        // given
        List<VpcDto> vpcs = List.of(
                VpcDto.builder()
                        .vpc(Vpc.builder()
                                .cidrBlock("172.31.0.0/16")
                                .instanceTenancy("default")
                                .build())
                        .enableDnsSupport(true)
                        .enableDnsHostnames(true)
                        .build()
            );

        Maps<Resource> resourceMaps = exportvpcs.getResourceMaps(vpcs);
        String actual = resourceMaps.unmarshall();
        log.debug("resourceMaps => \n{}", actual);
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/vpc/expected/vpc.tf"));
        assertEquals(expected, actual);
    }
}