package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.service.msk.ExportMskClusters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.kafka.KafkaClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class MskCommands extends AbstractCommands {

    private ExportMskClusters exportMskClusters;

    public MskCommands(ExportMskClusters exportMskClusters) {
        this.exportMskClusters = exportMskClusters;
    }

    @ShellMethod("Export terraform resources of MSK Clusters.")
    public void mskClusters(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportMskClusters.exportTerraform(KafkaClient.class, commonArgs);
    }

}
