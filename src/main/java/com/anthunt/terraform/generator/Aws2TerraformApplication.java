package com.anthunt.terraform.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Aws2TerraformApplication {

	public static void main(String[] args) {
		SpringApplication.run(Aws2TerraformApplication.class, args);

	}
}
