package com.anthunt.terraform.generator.aws;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.core.model.terraform.Terraform;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Provider;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import com.anthunt.terraform.generator.core.model.terraform.types.ProviderType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ActiveProfiles("custom")
@EntityScan("com.anthunt.terraform.generator.*")
@SpringBootTest(classes = {AmazonClients.class})
@SpringBootApplication
public class TerraformTest {

    public void contextLoads() {}

    @Test
    public void aws_ec2_to_terraform_string() {

    }

}
