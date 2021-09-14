package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.elb.ExportLoadBalancers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class ElbCommands extends AbstractCommands {

    private ExportLoadBalancers exportLoadBalancers;

    public ElbCommands(ExportLoadBalancers exportLoadBalancers) {
        this.exportLoadBalancers = exportLoadBalancers;
    }

    @ShellMethod("Export terraform resources of LoadBalancers.")
    public void loadBalancers(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportLoadBalancers.exportTerraform(ElasticLoadBalancingV2Client.class, commonArgs);
    }

}
