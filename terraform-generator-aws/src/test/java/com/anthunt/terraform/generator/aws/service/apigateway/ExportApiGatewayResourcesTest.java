package com.anthunt.terraform.generator.aws.service.apigateway;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSIntegration;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSMethod;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSResource;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSRestApiResource;
import com.anthunt.terraform.generator.aws.support.DisabledOnNoAwsCredentials;
import com.anthunt.terraform.generator.aws.support.TestDataFileUtils;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportApiGatewayResourcesTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportApiGatewayResources exportApiGatewayResources;

    private static ApiGatewayClient client;

    @BeforeAll
    public static void beforeAll() {
        exportApiGatewayResources = new ExportApiGatewayResources();
        exportApiGatewayResources.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getApiGatewayClient();
    }

    private List<AWSRestApiResource> getAwsRestApiResources() {
        AWSResource awsResource1 = AWSResource.builder()
                .restApiName("PetStore")
                .restApiId("12345abcde")
                .resource(software.amazon.awssdk.services.apigateway.model.Resource.builder()
                        .id("22vkob0b6j")
                        .build())
                .awsMethod(AWSMethod.builder()
                        .restApiName("PetStore")
                        .restApiId("12345abcde")
                        .resourceId("22vkob0b6j")
                        .method(GetMethodResponse.builder()
                                .httpMethod("GET")
                                .authorizationType("NONE")
                                .requestParameters(Map.of())
                                .build())
                        .awsIntegration(AWSIntegration.builder()
                                .restApiName("PetStore")
                                .restApiId("12345abcde")
                                .resourceId("22vkob0b6j")
                                .integration(GetIntegrationResponse.builder()
                                        .type("MOCK")
                                        .build())
                                .build())
                        .build())
                .build();

        AWSResource awsResource2 = AWSResource.builder()
                .restApiName("PetStore")
                .restApiId("12345abcde")
                .resource(software.amazon.awssdk.services.apigateway.model.Resource.builder()
                        .id("mvv3y4")
                        .parentId("px3r5v")
                        .pathPart("{petId}")
                        .build())
                .awsMethod(AWSMethod.builder()
                        .restApiName("PetStore")
                        .restApiId("12345abcde")
                        .resourceId("mvv3y4")
                        .method(GetMethodResponse.builder()
                                .httpMethod("GET")
                                .authorizationType("NONE")
                                .requestParameters(Map.of("method.request.path.petId", true))
                                .build())
                        .awsIntegration(AWSIntegration.builder()
                                .restApiName("PetStore")
                                .restApiId("12345abcde")
                                .resourceId("mvv3y4")
                                .integration(GetIntegrationResponse.builder()
                                        .httpMethod("GET")
                                        .type("HTTP")
                                        .connectionType("INTERNET")
                                        .uri("http://petstore.execute-api.ap-northeast-2.amazonaws.com/petstore/pets/{petId}")
                                        .httpMethod("GET")
                                        .requestParameters(Map.of("integration.request.path.petId", "method.request.path.petId"))
                                        .build())
                                .build())
                        .build())
                .awsMethod(AWSMethod.builder()
                        .restApiName("PetStore")
                        .restApiId("12345abcde")
                        .resourceId("mvv3y4")
                        .method(GetMethodResponse.builder()
                                .httpMethod("OPTIONS")
                                .authorizationType("NONE")
                                .requestParameters(Map.of("method.request.path.petId", true))
                                .build())
                        .awsIntegration(AWSIntegration.builder()
                                .restApiName("PetStore")
                                .restApiId("12345abcde")
                                .resourceId("mvv3y4")
                                .integration(GetIntegrationResponse.builder()
                                        .httpMethod("OPTIONS")
                                        .type("MOCK")
                                        .build())
                                .build())
                        .build())
                .build();

        AWSResource awsResource3 = AWSResource.builder()
                .restApiName("PetStore")
                .restApiId("12345abcde")
                .resource(software.amazon.awssdk.services.apigateway.model.Resource.builder()
                        .id("px3r5v")
                        .parentId("22vkob0b6j")
                        .pathPart("pets")
                        .build())
                .awsMethod(AWSMethod.builder()
                        .restApiName("PetStore")
                        .restApiId("12345abcde")
                        .resourceId("px3r5v")
                        .method(GetMethodResponse.builder()
                                .httpMethod("POST")
                                .authorizationType("NONE")
                                .build())
                        .awsIntegration(AWSIntegration.builder()
                                .restApiName("PetStore")
                                .restApiId("12345abcde")
                                .resourceId("px3r5v")
                                .integration(GetIntegrationResponse.builder()
                                        .httpMethod("POST")
                                        .type("HTTP")
                                        .connectionType("INTERNET")
                                        .uri("http://petstore.execute-api.ap-northeast-2.amazonaws.com/petstore/pets")
                                        .httpMethod("POST")
                                        .build())
                                .build())
                        .build())
                .awsMethod(AWSMethod.builder()
                        .restApiName("PetStore")
                        .restApiId("12345abcde")
                        .resourceId("px3r5v")
                        .method(GetMethodResponse.builder()
                                .httpMethod("GET")
                                .authorizationType("NONE")
                                .requestParameters(Map.of("method.request.querystring.type", false,
                                        "method.request.querystring.page", false))
                                .build())
                        .awsIntegration(AWSIntegration.builder()
                                .restApiName("PetStore")
                                .restApiId("12345abcde")
                                .resourceId("px3r5v")
                                .integration(GetIntegrationResponse.builder()
                                        .httpMethod("GET")
                                        .type("HTTP")
                                        .connectionType("INTERNET")
                                        .uri("http://petstore.execute-api.ap-northeast-2.amazonaws.com/petstore/pets")
                                        .httpMethod("GET")
                                        .requestParameters(Map.of("integration.request.querystring.page", "method.request.querystring.page",
                                                "integration.request.querystring.type", "method.request.querystring.type"))
                                        .build())
                                .build())

                        .build())
                .awsMethod(AWSMethod.builder()
                        .restApiName("PetStore")
                        .restApiId("12345abcde")
                        .resourceId("px3r5v")
                        .method(GetMethodResponse.builder()
                                .httpMethod("OPTIONS")
                                .authorizationType("NONE")
                                .build())
                        .awsIntegration(AWSIntegration.builder()
                                .restApiName("PetStore")
                                .restApiId("12345abcde")
                                .resourceId("px3r5v")
                                .integration(GetIntegrationResponse.builder()
                                        .type("MOCK")
                                        .httpMethod("OPTIONS")
                                        .build())
                                .build())

                        .build())
                .build();

        return List.of(
                AWSRestApiResource.builder()
                        .restApi(RestApi.builder()
                                .name("PetStore")
                                .id("12345abcde")
                                .description("Your first API with Amazon API Gateway. This is a sample API that integrates via HTTP with our demo Pet Store endpoints")
                                .apiKeySource(ApiKeySourceType.HEADER)
                                .disableExecuteApiEndpoint(false)
                                .endpointConfiguration(EndpointConfiguration.builder()
                                        .types(EndpointType.REGIONAL)
                                        .build())
                                .build()
                        )
                        .awsResources(List.of(awsResource1, awsResource2, awsResource3))
                        .build());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportApiGatewayResources.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {

        List<AWSRestApiResource> awsRestApis = getAwsRestApiResources();

        Maps<Resource> resourceMaps = exportApiGatewayResources.getResourceMaps(awsRestApis);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/ApigatewayResource.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/ApigatewayResource.cmd"));
        String actual = exportApiGatewayResources.getTFImport(getAwsRestApiResources()).script();

        assertEquals(expected, actual);
    }

}