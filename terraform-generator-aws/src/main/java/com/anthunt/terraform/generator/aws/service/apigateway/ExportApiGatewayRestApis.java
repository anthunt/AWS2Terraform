package com.anthunt.terraform.generator.aws.service.apigateway;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSDeployment;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSRestApi;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSStage;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportApiGatewayRestApis extends AbstractExport<ApiGatewayClient> {

    @Override
    protected Maps<Resource> export(ApiGatewayClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRestApi> awsRestApis = listAWSRestApis(client);
        return getResourceMaps(awsRestApis);
    }

    @Override
    protected TFImport scriptImport(ApiGatewayClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRestApi> awsRestApis = listAWSRestApis(client);
        return getTFImport(awsRestApis);
    }

    List<AWSRestApi> listAWSRestApis(ApiGatewayClient client) {
        GetRestApisResponse restApisResponse = client.getRestApis();
        return restApisResponse.items().stream()
                .peek(restApi -> log.debug("restApi => {}", restApi))
                .map(restApi -> AWSRestApi.builder()
                        .restApi(restApi)
                        .awsStages(client.getStages(GetStagesRequest.builder()
                                        .restApiId(restApi.id())
                                        .build())
                                .item().stream()
                                .map(stage -> AWSStage.builder()
                                        .stage(stage)
                                        .awsDeployment(AWSDeployment.builder()
                                                .restApiName(restApi.name())
                                                .deployment(client.getDeployment(GetDeploymentRequest.builder()
                                                        .restApiId(restApi.id())
                                                        .deploymentId(stage.deploymentId())
                                                        .build()))
                                                .build())
                                        .build())
                                .collect(Collectors.toList()))

                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSRestApi> awsRestApis) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSRestApi awsRestApi : awsRestApis) {
            RestApi restApi = awsRestApi.getRestApi();

            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api(awsRestApi.getTerraformResourceName())
                                    .name(awsRestApi.getResourceName())
                                    .argument("name", TFString.build(restApi.name()))
                                    .argument("description", TFString.build(restApi.description()))
                                    .argument("api_key_source", TFString.build(restApi.apiKeySourceAsString()))
                                    .argument("disable_execute_api_endpoint", TFBool.build(restApi.disableExecuteApiEndpoint()))
                                    .argument("endpoint_configuration", TFBlock.builder()
                                            .argument("types", TFList.build(
                                                    restApi.endpointConfiguration()
                                                            .typesAsStrings().stream()
                                                            .map(type -> TFString.builder().isLineIndent(false).value(type)
                                                                    .build())
                                                            .collect(Collectors.toList())
                                            ))
                                            .argument("vpc_endpoint_ids", TFList.build(
                                                    restApi.endpointConfiguration()
                                                            .vpcEndpointIds().stream()
                                                            .map(type -> TFString.build(
                                                                    MessageFormat.format("aws_vpc_endpoint.{0}.id", type)))
                                                            .collect(Collectors.toList())
                                            ))
                                            .build()
                                    )
                                    .build())
                    .build();

            awsRestApi.getAwsStages().forEach(awsStage -> {
                        Stage stage = awsStage.getStage();
                        resourceMapsBuilder.map(
                                Resource.builder()
                                        .api(awsStage.getTerraformResourceName())
                                        .name(awsStage.getResourceName())
                                        .argument("rest_api_id", TFExpression.build(
                                                MessageFormat.format("aws_api_gateway_rest_api.{0}.id", restApi.name())))
                                        .argument("deployment_id", TFExpression.build(
                                                MessageFormat.format("aws_api_gateway_deployment.{0}-{1}.id", restApi.name(), stage.deploymentId())))
                                        .argument("stage_name", TFString.build(stage.stageName()))
                                        .argument("xray_tracing_enabled", TFBool.build(stage.tracingEnabled()))
                                        .argumentIf(Optional.ofNullable(stage.accessLogSettings()).isPresent(),
                                                "access_log_settings",
                                                () -> TFBlock.builder()
                                                        .argument("destination_arn", TFString.build(stage.accessLogSettings().destinationArn()))
                                                        .argument("format", TFString.build(stage.accessLogSettings().format()))
                                                        .build())
                                        .build());

                        AWSDeployment awsDeployment = awsStage.getAwsDeployment();
                        GetDeploymentResponse deployment = awsDeployment.getDeployment();

                        resourceMapsBuilder.map(
                                Resource.builder()
                                        .api(awsDeployment.getTerraformResourceName())
                                        .name(awsDeployment.getResourceName())
                                        .argument("rest_api_id", TFExpression.build(
                                                MessageFormat.format("aws_api_gateway_rest_api.{0}.id", restApi.name())))
                                        .argument("stage_name", TFString.build(stage.stageName()))
                                        .argument("description", TFString.build(deployment.description()))
                                        .build());
                    }
            );


        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSRestApi> awsRestApis) {
        return TFImport.builder()
                .importLines(awsRestApis.stream()
                        .map(awsRestApi -> TFImportLine.builder()
                                .address(awsRestApi.getTerraformAddress())
                                .id(awsRestApi.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
        //TODO: aws_api_gateway_stage import 추가
        //TODO: aws_api_gateway_deployment import 추가
    }
}
