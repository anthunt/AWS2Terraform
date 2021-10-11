package com.anthunt.terraform.generator.aws.service.apigateway;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
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
import software.amazon.awssdk.services.apigateway.model.GetAccountResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportApiGatewayAccountTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportApiGatewayAccount exportApiGatewayAccount;

    private static ApiGatewayClient client;

    @BeforeAll
    public static void beforeAll() {
        exportApiGatewayAccount = new ExportApiGatewayAccount();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getApiGatewayClient();
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportApiGatewayAccount.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        GetAccountResponse account =
                GetAccountResponse.builder()
                        .cloudwatchRoleArn("arn:aws:iam::100020003000:role/APIGatewayPushToCloudWatchLogs")
                        .build();


        Maps<Resource> resourceMaps = exportApiGatewayAccount.getResourceMaps(account);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/Apigateway.tf")
        );
        assertEquals(expected, actual);
    }

}