package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.s3.ExportS3Buckets;
import com.anthunt.terraform.generator.aws.service.vpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.s3.S3Client;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class S3Commands extends AbstractCommands {

    @Autowired
    private ExportS3Buckets exportS3Buckets;


    @ShellMethod("Export terraform resources of s3 buckets")
    public void S3Buckets(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        this.exportS3Buckets.exportTerraform(S3Client.class, commonArgs);
    }

}
