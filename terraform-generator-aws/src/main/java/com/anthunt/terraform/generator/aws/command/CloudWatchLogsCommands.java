package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.service.cloudwatchlogs.ExportCloudWatchLogGroups;
import com.anthunt.terraform.generator.aws.service.cloudwatchlogs.ExportResourcePolicies;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class CloudWatchLogsCommands extends AbstractCommands {

    private ExportCloudWatchLogGroups exportCloudWatchLogGroups;

    private ExportResourcePolicies exportResourcePolicies;

    public CloudWatchLogsCommands(ExportCloudWatchLogGroups exportCloudWatchLogGroups, ExportResourcePolicies exportResourcePolicies) {
        this.exportCloudWatchLogGroups = exportCloudWatchLogGroups;
        this.exportResourcePolicies = exportResourcePolicies;
    }

    @ShellMethod("Export terraform resources of CloudWatch LogGroups.")
    public void cloudWatchLogGroups(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportCloudWatchLogGroups.exportTerraform(CloudWatchLogsClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of CloudWatch Logs Resource Policies.")
    public void resourcePolicies(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportResourcePolicies.exportTerraform(CloudWatchLogsClient.class, commonArgs);
    }

}
