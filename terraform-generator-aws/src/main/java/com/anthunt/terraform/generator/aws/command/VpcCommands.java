package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;

@Slf4j
@ShellComponent
public class VpcCommands extends AbstractCommands {

    @ShellMethod("Export terraform resources of vpcs")
    public void exportVpcs() {
        DescribeVpcsResponse response = clients.getEc2Client().describeVpcs();
        log.debug("vpcs=>{}", response);
        if (response.hasVpcs()) {
            System.out.println(response.vpcs());
        }
    }

}
