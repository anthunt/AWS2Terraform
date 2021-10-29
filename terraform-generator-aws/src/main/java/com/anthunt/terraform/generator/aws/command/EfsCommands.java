package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.efs.ExportEfses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.efs.EfsClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class EfsCommands extends AbstractCommands {

    private ExportEfses exportEfses;

    public EfsCommands(ExportEfses exportEfses) {
        this.exportEfses = exportEfses;
    }

    @ShellMethod("Export terraform resources of ECR Repository.")
    public void efsFileSystems(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportEfses.exportTerraform(EfsClient.class, commonArgs);
    }

}
