package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.client.AmazonClients;

public class AbstractCommands {

    protected AmazonClients clients;

    public AbstractCommands(AmazonClients clients) {
        this.clients = clients;
    }

}
