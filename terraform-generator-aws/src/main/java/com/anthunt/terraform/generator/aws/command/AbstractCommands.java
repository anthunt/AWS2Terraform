package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public abstract class AbstractCommands {

//    @ShellMethodAvailability({"export-ec2instances", "export-vpcs"})
//    public Availability clientCheck() {
//        return !"".equals(clients.getProfileName()) && clients.getRegion() != null
//                ? Availability.available() : Availability.unavailable("aws profile name or region is empty.");
//    }

}
