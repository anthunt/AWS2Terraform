package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.iam.ExportIamRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.iam.IamClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class IamCommands {

    private ExportIamRoles exportIamRoles;

    public IamCommands(ExportIamRoles exportIamRoles) {
        this.exportIamRoles = exportIamRoles;
    }

    @ShellMethod("Export terraform resources of iam.")
    public void iamRoles(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportIamRoles.exportTerraform(IamClient.class, commonArgs);
    }
}
