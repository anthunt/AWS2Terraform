package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.iam.ExportIamInstanceProfiles;
import com.anthunt.terraform.generator.aws.service.iam.ExportIamPolicies;
import com.anthunt.terraform.generator.aws.service.iam.ExportIamRolePolicies;
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

    private ExportIamRolePolicies exportIamRolePolicies;

    private ExportIamPolicies exportIamPolicies;

    private ExportIamPolicies exportIamPolicyAttachment;

    private ExportIamInstanceProfiles exportIamInstanceProfiles;

    public IamCommands(ExportIamRoles exportIamRoles, ExportIamRolePolicies exportIamRolePolicies, ExportIamPolicies exportIamPolicies, ExportIamPolicies exportIamPolicyAttachment, ExportIamInstanceProfiles exportIamInstanceProfiles) {
        this.exportIamRoles = exportIamRoles;
        this.exportIamRolePolicies = exportIamRolePolicies;
        this.exportIamPolicies = exportIamPolicies;
        this.exportIamPolicyAttachment = exportIamPolicyAttachment;
        this.exportIamInstanceProfiles = exportIamInstanceProfiles;
    }

    @ShellMethod("Export terraform resources of iamRoles.")
    public void iamRoles(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportIamRoles.exportTerraform(IamClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of iamRolePolicies.")
    public void iamRolePolicies(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportIamRolePolicies.exportTerraform(IamClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of iamPolicies.")
    public void iamPolicies(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportIamPolicies.exportTerraform(IamClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of iamPolicyAttachment.")
    public void iamPolicyAttachment(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportIamPolicyAttachment.exportTerraform(IamClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of iamInstanceProfiles.")
    public void iamInstanceProfiles(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportIamInstanceProfiles.exportTerraform(IamClient.class, commonArgs);
    }
}
