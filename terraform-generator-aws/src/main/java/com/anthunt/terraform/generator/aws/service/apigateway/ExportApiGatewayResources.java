package com.anthunt.terraform.generator.aws.service.apigateway;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSIntegration;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSMethod;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSResource;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSRestApiResource;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFBool;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFExpression;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFMap;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportApiGatewayResources extends AbstractExport<ApiGatewayClient> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "ApiGatewayResources";

    @Override
    protected Maps<Resource> export(ApiGatewayClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRestApiResource> awsRestApiResources = listAWSRestApiResources(client);
        return getResourceMaps(awsRestApiResources);
    }

    @Override
    protected TFImport scriptImport(ApiGatewayClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRestApiResource> awsRestApiResources = listAWSRestApiResources(client);
        return getTFImport(awsRestApiResources);
    }

    @Override
    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSRestApiResource> listAWSRestApiResources(ApiGatewayClient client) {
        GetRestApisResponse restApisResponse = client.getRestApis();
        return restApisResponse.items().stream()
                .peek(restApi -> log.debug("restApi => {}", restApi))
                .map(restApi -> AWSRestApiResource.builder()
                        .restApi(restApi)
                        .awsResources(client.getResources(GetResourcesRequest.builder()
                                        .restApiId(restApi.id())
                                        .build())
                                .items().stream()
                                .map(resource -> AWSResource.builder()
                                        .resource(resource)
                                        .restApiName(restApi.name())
                                        .restApiId(restApi.id())
                                        .awsMethods(resource.resourceMethods().keySet().stream()
                                                .map(methodName -> AWSMethod.builder()
                                                        .restApiId(restApi.id())
                                                        .resourceId(resource.id())
                                                        .method(client.getMethod(GetMethodRequest.builder()
                                                                .restApiId(restApi.id())
                                                                .resourceId(resource.id())
                                                                .httpMethod(methodName)
                                                                .build()))
                                                        .awsIntegration(AWSIntegration.builder()
                                                                .restApiName(restApi.name())
                                                                .restApiId(restApi.id())
                                                                .resourceId(resource.id())
                                                                .integration(client.getIntegration(GetIntegrationRequest.builder()
                                                                        .restApiId(restApi.id())
                                                                        .resourceId(resource.id())
                                                                        .httpMethod(methodName)
                                                                        .build()))
                                                                .build())
                                                        .build())
                                                .peek(method -> log.debug("method => {}", method))
                                                .collect(Collectors.toList()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSRestApiResource> awsRestApiResources) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSRestApiResource awsRestApiResource : awsRestApiResources) {
            RestApi restApi = awsRestApiResource.getRestApi();

            @SuppressWarnings("OptionalGetWithoutIsPresent")
            String rootResourceId = awsRestApiResource.getAwsResources().stream()
                    .filter(awsResource -> awsResource.getResource().parentId() == null)
                    .findFirst().get()
                    .getResource()
                    .id();

            awsRestApiResource.getAwsResources().stream()
                    .filter(awsResource -> awsResource.getResource().parentId() != null)
                    .forEach(awsResource -> {
                        software.amazon.awssdk.services.apigateway.model.Resource resource = awsResource.getResource();
                        resourceMapsBuilder.map(
                                Resource.builder()
                                        .api(awsRestApiResource.getTerraformResourceName())
                                        .name(awsResource.getResourceName())
                                        .argument("rest_api_id ", TFExpression.build(
                                                MessageFormat.format("aws_api_gateway_rest_api.{0}.id", restApi.name())))
                                        .argument("parent_id", resource.parentId().equals(rootResourceId) ?
                                                TFExpression.build(MessageFormat.format("aws_api_gateway_rest_api.{0}.root_resource_id", restApi.name()))
                                                : TFExpression.build(MessageFormat.format("aws_api_gateway_resource.{0}-{1}.id", restApi.name(), resource.parentId())))
                                        .argument("path_part ", TFString.build(resource.pathPart()))
                                        .build());
                        awsResource.getAwsMethods().forEach(awsMethod -> {
                                    GetMethodResponse method = awsMethod.getMethod();
                                    resourceMapsBuilder.map(
                                            Resource.builder()
                                                    .api(awsMethod.getTerraformResourceName())
                                                    .name(awsMethod.getResourceName())
                                                    .argument("rest_api_id", TFExpression.build(
                                                            MessageFormat.format("aws_api_gateway_rest_api.{0}.id", restApi.name())))
                                                    .argument("resource_id", TFExpression.build(
                                                            MessageFormat.format("aws_api_gateway_resource.{0}-{1}.id", restApi.name(), resource.id())))
                                                    .argument("http_method", TFString.build(method.httpMethod()))
                                                    .argument("authorization", TFString.build(method.authorizationType()))
                                                    .argument("authorizer_id", TFString.build(method.authorizerId()))
                                                    .argument("request_parameters", TFMap.build(
                                                            method.requestParameters().entrySet().stream()
                                                                    .collect(Collectors.toMap(Map.Entry::getKey, parameter -> TFBool.build(parameter.getValue())))
                                                    ))
                                                    .build());

                                    AWSIntegration awsIntegration = awsMethod.getAwsIntegration();
                                    GetIntegrationResponse integration = awsIntegration.getIntegration();

                                    resourceMapsBuilder.map(
                                            Resource.builder()
                                                    .api(awsIntegration.getTerraformResourceName())
                                                    .name(awsIntegration.getResourceName())
                                                    .argument("rest_api_id", TFExpression.build(
                                                            MessageFormat.format("aws_api_gateway_rest_api.{0}.id", restApi.name())))
                                                    .argument("resource_id", TFExpression.build(
                                                            MessageFormat.format("aws_api_gateway_resource.{0}-{1}.id", restApi.name(), resource.id())))
                                                    .argument("http_method", TFString.build(method.httpMethod()))
                                                    .argument("type", TFString.build(integration.typeAsString()))
                                                    .argument("connection_type", TFString.build(integration.connectionTypeAsString()))
                                                    .argument("connection_id", TFString.build(integration.connectionId()))
                                                    .argument("uri", TFString.build(integration.uri()))
                                                    .argument("integration_http_method", TFString.build(integration.httpMethod()))
                                                    .argument("request_parameters", TFMap.build(
                                                            integration.requestParameters().entrySet().stream()
                                                                    .collect(Collectors.toMap(Map.Entry::getKey, parameter -> TFString.build(parameter.getValue())))
                                                    ))
                                                    .build());
                                }
                        );
                    });
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSRestApiResource> awsRestApiResources) {
        TFImport.TFImportBuilder tfImportBuilder = TFImport.builder();

        for (AWSRestApiResource awsRestApiResource : awsRestApiResources) {

            awsRestApiResource.getAwsResources().stream()
                    .filter(awsResource -> awsResource.getResource().parentId() != null)
                    .forEach(awsResource -> {
                        tfImportBuilder.importLine(TFImportLine.builder()
                                .address(awsResource.getTerraformAddress())
                                .id(awsResource.getResourceId())
                                .build());

                        awsResource.getAwsMethods().forEach(awsMethod -> {
                            AWSIntegration awsIntegration = awsMethod.getAwsIntegration();

                            tfImportBuilder.importLine(TFImportLine.builder()
                                    .address(awsMethod.getTerraformAddress())
                                    .id(awsMethod.getResourceId())
                                    .build());

                            tfImportBuilder.importLine(TFImportLine.builder()
                                    .address(awsIntegration.getTerraformAddress())
                                    .id(awsIntegration.getResourceId())
                                    .build());
                        });
                    });
        }
        return tfImportBuilder.build();
    }
}
