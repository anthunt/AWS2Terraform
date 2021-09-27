package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.rds.ExportRdsClusterParameterGroups;
import com.anthunt.terraform.generator.aws.service.rds.ExportRdsClusters;
import com.anthunt.terraform.generator.aws.service.rds.ExportRdsOptionGroups;
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

    private ExportRdsClusterParameterGroups exportRdsClusterParameterGroups;

    private ExportRdsOptionGroups exportRdsOptionGroups;

    public RdsCommands(ExportRdsClusters exportRdsClusters, ExportRdsClusterParameterGroups exportRdsClusterParameterGroups, ExportRdsOptionGroups exportRdsOptionGroups) {
        this.exportRdsClusters = exportRdsClusters;
        this.exportRdsClusterParameterGroups = exportRdsClusterParameterGroups;
        this.exportRdsOptionGroups = exportRdsOptionGroups;
    }

    @ShellMethod("Export terraform resources of rds clusters.")
    public void rdsClusters(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportRdsClusters.exportTerraform(RdsClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of rds clusters parameter groups.")
    public void rdsClusterParameterGroups(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportRdsClusterParameterGroups.exportTerraform(RdsClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of rds option groups.")
    public void rdsOptionGroups(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportRdsOptionGroups.exportTerraform(RdsClient.class, commonArgs);
    }

}
