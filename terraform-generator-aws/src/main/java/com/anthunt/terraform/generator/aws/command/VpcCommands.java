package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.vpc.*;
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

    @Autowired
    private ExportEgressOnlyInternetGateways exportEgressOnlyInternetGateways;

    @Autowired
    private ExportSubnets exportSubnets;

    @Autowired
    private ExportRouteTables exportRouteTables;

    @ShellMethod("Export terraform resources of vpcs")
    public void vpcs(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        this.exportVpcs.exportTerraform(Ec2Client.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of InternetGateways")
    public void internetGateways(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        this.exportInternetGateways.exportTerraform(Ec2Client.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of NatGateways")
    public void natGateways(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        this.exportNatGateways.exportTerraform(Ec2Client.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of EgressOnlyInternetGateways")
    public void egressOnlyInternetGateways(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        this.exportEgressOnlyInternetGateways.exportTerraform(Ec2Client.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of Subnets")
    public void subnets(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        this.exportSubnets.exportTerraform(Ec2Client.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of RouteTables")
    public void RouteTables(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        this.exportRouteTables.exportTerraform(Ec2Client.class, commonArgs);
    }
}
