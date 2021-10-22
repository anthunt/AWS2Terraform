package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.kms.ExportKmsKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.kms.KmsClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class KmsCommands extends AbstractCommands {

    private ExportKmsKeys exportKmsKeys;

    public KmsCommands(ExportKmsKeys exportKmsKeys) {
        this.exportKmsKeys = exportKmsKeys;
    }

    @ShellMethod("Export terraform resources of kms keys.")
    public void kmsKeys(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportKmsKeys.exportTerraform(KmsClient.class, commonArgs);
    }
}

