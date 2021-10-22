package com.anthunt.terraform.generator.aws.service.apigateway;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSAccount;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.GetAccountResponse;

import java.util.Optional;

@Slf4j
@Service
public class ExportApiGatewayAccount extends AbstractExport<ApiGatewayClient> {

    @Override
    protected Maps<Resource> export(ApiGatewayClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        AWSAccount awsAccount = getAccount(client);
        return getResourceMaps(awsAccount);
    }

    @Override
    protected TFImport scriptImport(ApiGatewayClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        AWSAccount awsAccount = getAccount(client);
        return getTFImport(awsAccount);
    }

    AWSAccount getAccount(ApiGatewayClient client) {
        return AWSAccount.builder().account(client.getAccount()).build();
    }

    Maps<Resource> getResourceMaps(AWSAccount awsAccount) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        GetAccountResponse account = awsAccount.getAccount();

        if (Optional.ofNullable(account.cloudwatchRoleArn()).isPresent()) {
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api(awsAccount.getTerraformResourceName())
                            .name(awsAccount.getResourceName())
                            .argument("cloudwatch_role_arn", TFString.build(account.cloudwatchRoleArn()))
                            .build()
            );
        }


        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(AWSAccount awsAccount) {
        return TFImport.builder()
                .importLine(TFImportLine.builder()
                        .address(awsAccount.getTerraformAddress())
                        .id(awsAccount.getResourceId())
                        .build())
                .build();
    }
}
