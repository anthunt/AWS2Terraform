package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.vpc.ExportVpcs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class VpcCommands extends AbstractCommands {

    @Autowired
    private ExportVpcs exportVpcs;

    @ShellMethod("Export terraform resources of vpcs")
    public void exportVpcs(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportVpcs.exportTerraform(Ec2Client.class, commonArgs);
    }

}
