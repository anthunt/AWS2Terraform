package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;

@Slf4j
@ShellComponent
public class ClientCommands {

    private AmazonClients clients;

    public ClientCommands(AmazonClients clients) {
        this.clients = clients;
    }

    @ShellMethod("set-profile")
    public void setProfile(String profileName) {
        clients.setProfileName(profileName);
        System.out.println("Profile is changed into " + clients.getProfileName());
    }

    @ShellMethod("set-region")
    public void setRegion(String region) {
        clients.setRegion(Region.of(region));
        System.out.println("Region is changed into " + clients.getRegion());
    }
}
