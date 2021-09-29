package com.anthunt.terraform.generator.aws.service.eks;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.eks.model.AWSEksCluster;
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
import software.amazon.awssdk.services.eks.EksClient;
import software.amazon.awssdk.services.eks.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportEksClustersTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static ExportEksClusters exportEksClusters;

    @BeforeAll
    public static void beforeAll() {
        exportEksClusters = new ExportEksClusters();
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        EksClient eksClient = amazonClients.getEksClient();
        Maps<Resource> export = exportEksClusters.export(eksClient, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        List<AWSEksCluster> awsEksClusters = List.of(
                AWSEksCluster.builder()
                        .cluster(Cluster.builder()
                                .name("eks-dev-app-cluster")
                                .roleArn("arn:aws:iam::100020003000:role/eks-cluster-role")
                                .resourcesVpcConfig(VpcConfigResponse.builder()
                                        .endpointPrivateAccess(false)
                                        .endpointPublicAccess(true)
                                        .publicAccessCidrs("0.0.0.0/0")
                                        .securityGroupIds("sg-010fc4d6910de29ce")
                                        .subnetIds("subnet-0f58e2bf1ada4d5c0", "subnet-003e5f077d31b5163")
                                        .build())
                                .kubernetesNetworkConfig(KubernetesNetworkConfigResponse.builder()
                                        .serviceIpv4Cidr("10.100.0.0/16")
                                        .build())
                                .version("1.20")
                                .logging(Logging.builder()
                                        .clusterLogging(LogSetup.builder()
                                                .types(LogType.API, LogType.AUDIT, LogType.AUTHENTICATOR, LogType.CONTROLLER_MANAGER, LogType.SCHEDULER)
                                                .build())
                                        .build())
                                .encryptionConfig(EncryptionConfig.builder()
                                        .provider(Provider.builder().keyArn("arn:aws:kms:ap-northeast-2:100020003000:key/10002000-aba9-49ae-8121-2f9411bfa69f").build())
                                        .resources("secrets")
                                        .build())

                                .build())
                        .tag("Name", "eks-dev-app-cluster")
                        .build()
        );

        Maps<Resource> resourceMaps = exportEksClusters.getResourceMaps(awsEksClusters);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/eks/expected/EksCluster.tf")
        );
        assertEquals(expected, actual);
    }

}