package com.anthunt.terraform.generator.aws;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
