package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.service.vpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.ec2.Ec2Client;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class VpcCommands extends AbstractCommands {

    private final ExportVpcs exportVpcs;

    private final ExportInternetGateways exportInternetGateways;

    private final ExportNatGateways exportNatGateways;

    private final ExportEgressOnlyInternetGateways exportEgressOnlyInternetGateways;

    private final ExportSubnets exportSubnets;

    private final ExportRouteTables exportRouteTables;

    private final ExportSecurityGroups exportSecurityGroups;

    public VpcCommands(ExportVpcs exportVpcs, ExportInternetGateways exportInternetGateways, ExportNatGateways exportNatGateways, ExportEgressOnlyInternetGateways exportEgressOnlyInternetGateways, ExportSubnets exportSubnets, ExportRouteTables exportRouteTables, ExportSecurityGroups exportSecurityGroups) {
        this.exportVpcs = exportVpcs;
        this.exportInternetGateways = exportInternetGateways;
        this.exportNatGateways = exportNatGateways;
        this.exportEgressOnlyInternetGateways = exportEgressOnlyInternetGateways;
        this.exportSubnets = exportSubnets;
        this.exportRouteTables = exportRouteTables;
        this.exportSecurityGroups = exportSecurityGroups;
    }

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

    @ShellMethod("Export terraform resources of SecurityGroups")
    public void SecurityGroups(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        this.exportSecurityGroups.exportTerraform(Ec2Client.class, commonArgs);
    }
}
