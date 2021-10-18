package com.anthunt.terraform.generator.aws.service.apigateway;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.GetAccountResponse;

import java.text.MessageFormat;
import java.util.Optional;

@Slf4j
@Service
public class ExportApiGatewayAccount extends AbstractExport<ApiGatewayClient> {

    @Override
    protected Maps<Resource> export(ApiGatewayClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        GetAccountResponse account = getAccount(client);
        return getResourceMaps(account);
    }

    @Override
    protected TFImport scriptImport(ApiGatewayClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        GetAccountResponse account = getAccount(client);
        return getTFImport(account);
    }

    GetAccountResponse getAccount(ApiGatewayClient client) {
        return client.getAccount();
    }

    Maps<Resource> getResourceMaps(GetAccountResponse accountResponse) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        if (Optional.ofNullable(accountResponse.cloudwatchRoleArn()).isPresent()) {
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_api_gateway_account")
                            .name(getResourceName(accountResponse))
                            .argument("cloudwatch_role_arn", TFString.build(accountResponse.cloudwatchRoleArn()))
                            .build()
            );
        }


        return resourceMapsBuilder.build();
    }

    private String getResourceName(GetAccountResponse accountResponse) {
        return MessageFormat.format("{0}-{1}",
                "account",
                accountResponse.cloudwatchRoleArn().split(":")[4]);
    }

    TFImport getTFImport(GetAccountResponse account) {
        return TFImport.builder()
                .importLine(TFImportLine.builder()
                        .address(MessageFormat.format("{0}.{1}",
                                "aws_api_gateway_account",
                                getResourceName(account)))
                        .id("api-gateway-account")
                        .build())
                .build();
    }
}
