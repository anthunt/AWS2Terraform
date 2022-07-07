package com.anthunt.terraform.generator.aws.service.elasticsearch;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.elasticsearch.model.AWSElasticsearchDomain;
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
import software.amazon.awssdk.services.elasticsearch.ElasticsearchClient;
import software.amazon.awssdk.services.elasticsearch.model.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportElasticsearchDomainsTest {

    private static ExportElasticsearchDomains exportElasticsearchDomains;
    private static ElasticsearchClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportElasticsearchDomains = new ExportElasticsearchDomains();
        exportElasticsearchDomains.setDelayBetweenApis(0);
        AmazonClients.setProfileName("default");
        AmazonClients.setRegion(Region.AP_NORTHEAST_2);
        client = AmazonClients.getElasticsearchClient();
    }

    private List<AWSElasticsearchDomain> getAwsElasticsearchDomains() {
        return List.of(
                AWSElasticsearchDomain.builder()
                        .elasticsearchDomainStatus(ElasticsearchDomainStatus.builder()
                                .domainName("test-domain")
                                .elasticsearchVersion("OpenSearch_1.0")
                                .elasticsearchClusterConfig(ElasticsearchClusterConfig.builder()
                                        .dedicatedMasterEnabled(false).instanceType("t3.small.elasticsearch")
                                        .instanceCount(1).build())
                                .vpcOptions(VPCDerivedInfo.builder()
                                        .subnetIds("subnet-09cb7762").securityGroupIds("sg-23321f59").build())
                                .advancedOptions(Map.of("override_main_response_version", "false",
                                        "rest.action.multi.allow_explicit_index", "true"))
                                .ebsOptions(EBSOptions.builder()
                                        .ebsEnabled(true).volumeSize(10).volumeType("gp2").build())
                                .nodeToNodeEncryptionOptions(NodeToNodeEncryptionOptions.builder().enabled(true).build())
                                .encryptionAtRestOptions(EncryptionAtRestOptions.builder().enabled(true).build())
                                .advancedSecurityOptions(AdvancedSecurityOptions.builder().enabled(true)
                                        .internalUserDatabaseEnabled(true).build())
                                .cognitoOptions(CognitoOptions.builder().enabled(false).build())
                                .domainEndpointOptions(DomainEndpointOptions.builder().enforceHTTPS(true)
                                        .tlsSecurityPolicy("Policy-Min-TLS-1-0-2019-07").build())
                                .accessPolicies(TestDataFileUtils
                                        .asString(resourceLoader
                                                .getResource("testData/aws/input/ElasticsearchDomainAccessPolicyDocument.json")))
                                .build())
                        .tag(Tag.builder().key("Name").value("Test-ES").build())
                        .build()
        );
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        Maps<Resource> export = exportElasticsearchDomains.export(client, null, null);
        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void listAwsElasticsearchDomains() {
        List<AWSElasticsearchDomain> awsElasticsearchDomains = exportElasticsearchDomains.listAwsElasticsearchDomains(client);
        log.debug("awsElasticsearchDomains => {}", awsElasticsearchDomains);
    }

    @Test
    public void getResourceMaps() {
        List<AWSElasticsearchDomain> awsElasticsearchDomains = getAwsElasticsearchDomains();

        Maps<Resource> resourceMaps = exportElasticsearchDomains.getResourceMaps(awsElasticsearchDomains);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/Elasticsearch.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/Elasticsearch.cmd"));
        String actual = exportElasticsearchDomains.getTFImport(getAwsElasticsearchDomains()).script();

        assertEquals(expected, actual);
    }
}