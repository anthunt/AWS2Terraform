package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.service.eks.ExportEksClusters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.eks.EksClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class EksCommands extends AbstractCommands {

    private ExportEksClusters exportEksClusters;

    public EksCommands(ExportEksClusters exportEksClusters) {
        this.exportEksClusters = exportEksClusters;
    }

    @ShellMethod("Export terraform resources of ec2 instances.")
    public void eksClusters(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportEksClusters.exportTerraform(EksClient.class, commonArgs);
    }

}
