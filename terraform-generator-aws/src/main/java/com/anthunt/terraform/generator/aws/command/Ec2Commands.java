package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.ec2.ExportInstances;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;

@Slf4j
@ShellComponent
public class Ec2Commands extends AbstractCommands {

    public Ec2Commands(AmazonClients clients) {
        super(clients);
    }

    @Autowired
    private ExportInstances exportInstances;

    @ShellMethod("Export terraform resources of ec2 instances.")
    public void exportEc2Instances() {
        exportInstances.exportTerraform(clients.getProfileName(), clients.getRegion(), clients.getEc2Client());
    }

}
