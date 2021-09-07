package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.vpc.dto.SecurityGroupDto;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

import java.util.List;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class},
        properties = { "logging.level.com.anthunt.terraform.generator.aws=DEBUG" }
)
class ExportSecurityGroupsTest {

    @Test
    void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();

        ExportSecurityGroups exportSecurityGroups = new ExportSecurityGroups();
        Maps<Resource> export = exportSecurityGroups.export(ec2Client, null, null);

        log.debug("result => {}", export.unmarshall());
    }

    @Test
    void getSecurityGroups() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        Ec2Client ec2Client = amazonClients.getEc2Client();

        ExportSecurityGroups exportSecurityGroups = new ExportSecurityGroups();
        List<SecurityGroupDto> securityGroups = exportSecurityGroups.getSecurityGroups(ec2Client);
        log.debug("securityGroups => {}", securityGroups);
    }
}