package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.s3.ExportS3Buckets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.s3.S3Client;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class S3Commands extends AbstractCommands {

    private ExportS3Buckets exportS3Buckets;

    public S3Commands(ExportS3Buckets exportS3Buckets) {
        this.exportS3Buckets = exportS3Buckets;
    }

    @ShellMethod("Export terraform resources of s3 buckets.")
    public void s3Buckets(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportS3Buckets.exportTerraform(S3Client.class, commonArgs);
    }

}
