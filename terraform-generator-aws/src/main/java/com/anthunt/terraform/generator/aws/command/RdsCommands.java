package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.rds.ExportRdsClusters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.rds.RdsClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class RdsCommands extends AbstractCommands {

    private ExportRdsClusters exportRdsClusters;

    public RdsCommands(ExportRdsClusters exportRdsClusters) {
        this.exportRdsClusters = exportRdsClusters;
    }

    @ShellMethod("Export terraform resources of ec2 instances.")
    public void rdsClusters(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportRdsClusters.exportTerraform(RdsClient.class, commonArgs);
    }

}
