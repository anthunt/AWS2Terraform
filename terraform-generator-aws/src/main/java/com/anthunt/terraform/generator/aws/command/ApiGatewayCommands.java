package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.service.apigateway.ExportApiGatewayResources;
import com.anthunt.terraform.generator.aws.service.apigateway.ExportApiGatewayRestApis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class ApiGatewayCommands extends AbstractCommands {

    private ExportApiGatewayRestApis exportApiGatewayRestApis;

    private ExportApiGatewayResources exportApiGatewayResources;

    public ApiGatewayCommands(ExportApiGatewayRestApis exportApiGatewayRestApis, ExportApiGatewayResources exportApiGatewayResources) {
        this.exportApiGatewayRestApis = exportApiGatewayRestApis;
        this.exportApiGatewayResources = exportApiGatewayResources;
    }

    @ShellMethod("Export terraform resources of ApiGateway RestApis.")
    public void apiGatewayRestApis(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportApiGatewayRestApis.exportTerraform(ApiGatewayClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of ApiGateway Resources.")
    public void apiGatewayResources(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportApiGatewayResources.exportTerraform(ApiGatewayClient.class, commonArgs);
    }
}
