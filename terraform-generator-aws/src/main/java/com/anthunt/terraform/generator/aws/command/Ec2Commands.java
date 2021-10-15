package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.ec2.ExportInstances;
import com.anthunt.terraform.generator.aws.service.ec2.ExportlaunchTemplates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.ec2.Ec2Client;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class Ec2Commands extends AbstractCommands {

    private ExportInstances exportInstances;

    private ExportlaunchTemplates exportlaunchTemplates;

    public Ec2Commands(ExportInstances exportInstances, ExportlaunchTemplates exportlaunchTemplates) {
        this.exportInstances = exportInstances;
        this.exportlaunchTemplates = exportlaunchTemplates;
    }

    @ShellMethod("Export terraform resources of ec2 instances.")
    public void ec2Instances(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportInstances.exportTerraform(Ec2Client.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of launch templates.")
    public void launchTemplates(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportlaunchTemplates.exportTerraform(Ec2Client.class, commonArgs);
    }

}
