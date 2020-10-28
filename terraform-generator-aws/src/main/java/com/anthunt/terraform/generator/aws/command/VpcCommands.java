package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.vpc.ExportInternetGateways;
import com.anthunt.terraform.generator.aws.service.vpc.ExportNatGateways;
import com.anthunt.terraform.generator.aws.service.vpc.ExportVpcs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.ec2.Ec2Client;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class VpcCommands extends AbstractCommands {

    @Autowired
    private ExportVpcs exportVpcs;

    @Autowired
    private ExportInternetGateways exportInternetGateways;

    @Autowired
    private ExportNatGateways exportNatGateways;

    @ShellMethod("Export terraform resources of vpcs")
    public void vpcs(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportVpcs.exportTerraform(Ec2Client.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of InternetGateways")
    public void internetGateways(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportInternetGateways.exportTerraform(Ec2Client.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of NatGateways")
    public void natGateways(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportNatGateways.exportTerraform(Ec2Client.class, commonArgs);
    }

}
