package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import software.amazon.awssdk.regions.Region;

@Slf4j
@ShellComponent
public class ClientCommands {

    private AmazonClients clients;

    public ClientCommands(AmazonClients clients) {
        this.clients = clients;
    }

    @ShellMethod(key = "get-profile", value = "Get Profile Name.")
    public void getProfileName() {
        System.out.println("Profile => " + clients.getProfileName());
    }

    @ShellMethod(key = "set-profile", value = "Set Profile Name.")
    public void setProfileName(String profileName) {
        clients.setProfileName(profileName);
        System.out.println("Profile is changed into " + clients.getProfileName());
    }

    @ShellMethod(key = "get-region", value = "Get Region Id.")
    public void getRegion() {
        System.out.println("Region => " + clients.getRegion());
    }

    @ShellMethod(key = "set-region", value = "Set Region Id.")
    public void setRegion(String region) {
        clients.setRegion(Region.of(region));
        System.out.println("Region is changed into " + clients.getRegion());
    }

}
