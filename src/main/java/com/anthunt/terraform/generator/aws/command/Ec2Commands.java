package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;

@Slf4j
@ShellComponent
public class Ec2Commands {

    private AmazonClients clients;

    public Ec2Commands(AmazonClients clients) {
        this.clients = clients;
    }

    @ShellMethod("describe-instances")
    public void describeInstances() {
        DescribeInstancesResponse response = clients.getEc2Client().describeInstances();
        log.debug("response=>{}", response.reservations());
        System.out.println(response.reservations());
    }

    @ShellMethod("describe-vpcs")
    public void describeVpcs() {
        DescribeVpcsResponse response = clients.getEc2Client().describeVpcs();
        log.debug("vpcs=>{}", response);
        if (response.hasVpcs()) {
            System.out.println(response.vpcs());
        }
    }
}
