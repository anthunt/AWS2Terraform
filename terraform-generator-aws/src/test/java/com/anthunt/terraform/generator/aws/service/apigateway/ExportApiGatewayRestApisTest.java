package com.anthunt.terraform.generator.aws.service.apigateway;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSDeployment;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSRestApi;
import com.anthunt.terraform.generator.aws.service.apigateway.model.AWSStage;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportApiGatewayRestApisTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportApiGatewayRestApis exportApigatewayRestApis;

    private static ApiGatewayClient client;

    @BeforeAll
    public static void beforeAll() {
        exportApigatewayRestApis = new ExportApiGatewayRestApis();
        exportApigatewayRestApis.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getApiGatewayClient();
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportApigatewayRestApis.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        List<AWSRestApi> awsRestApis = List.of(
                AWSRestApi.builder()
                        .restApi(RestApi.builder()
                                .name("PetStore")
                                .description("Your first API with Amazon API Gateway. This is a sample API that integrates via HTTP with our demo Pet Store endpoints")
                                .apiKeySource(ApiKeySourceType.HEADER)
                                .disableExecuteApiEndpoint(false)
                                .endpointConfiguration(EndpointConfiguration.builder()
                                        .types(EndpointType.REGIONAL)
                                        .build())
                                .build()
                        )
                        .awsStage(AWSStage.builder()
                                .stage(Stage.builder()
                                        .stageName("PROD")
                                        .deploymentId("0t5yci")
                                        .tracingEnabled(false)
                                        .build())
                                .awsDeployment(AWSDeployment.builder()
                                        .restApiName("PetStore")
                                        .deployment(
                                                GetDeploymentResponse.builder()
                                                        .id("0t5yci")
                                                        .description("test deploy")
                                                        .build())
                                        .build())
                                .build())
                        .build());

        Maps<Resource> resourceMaps = exportApigatewayRestApis.getResourceMaps(awsRestApis);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/ApigatewayRestApi.tf")
        );
        assertEquals(expected, actual);
    }

}