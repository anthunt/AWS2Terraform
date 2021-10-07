package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.ecr.ExportEcrRepository;
import com.anthunt.terraform.generator.aws.service.efs.ExportEfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.efs.EfsClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class EfsCommands extends AbstractCommands {

    private ExportEfs exportEfs;

    public EfsCommands(ExportEfs exportEfs) {
        this.exportEfs = exportEfs;
    }

    @ShellMethod("Export terraform resources of ECR Repository.")
    public void efsFileSystems(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportEfs.exportTerraform(EfsClient.class, commonArgs);
    }

}
