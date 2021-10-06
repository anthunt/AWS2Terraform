package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.cloudwatchlogs.ExportCloudWatchLogGroups;
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

    public CloudWatchLogsCommands(ExportCloudWatchLogGroups exportCloudWatchLogGroups) {
        this.exportCloudWatchLogGroups = exportCloudWatchLogGroups;
    }

    @ShellMethod("Export terraform resources of CloudWatch LogGroups.")
    public void cloudWatchLogGroups(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportCloudWatchLogGroups.exportTerraform(CloudWatchLogsClient.class, commonArgs);
    }

}
