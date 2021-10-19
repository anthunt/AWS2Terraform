package com.anthunt.terraform.generator.aws.service.eks;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.service.eks.model.AWSEksCluster;
import com.anthunt.terraform.generator.aws.service.eks.model.AWSEksNodeGroup;
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

    private static EksClient client;

    @BeforeAll
    public static void beforeAll() {
        exportEksClusters = new ExportEksClusters();
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AP_NORTHEAST_2).build();
        client = amazonClients.getEksClient();
    }

    private List<AWSEksCluster> getAwsEksClusters() {
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
                        .addon(Addon.builder()
                                .clusterName("eks-dev-app-cluster")
                                .addonName("kube-proxy")
                                .addonVersion("v1.20.4-eksbuild.2")
                                .build())
                        .addon(Addon.builder()
                                .clusterName("eks-dev-app-cluster")
                                .addonName("vpc-cni")
                                .addonVersion("v1.7.10-eksbuild.1")
                                .build())
                        .awsEksNodeGroup(AWSEksNodeGroup.builder()
                                .nodegroup(Nodegroup.builder()
                                        .clusterName("eks-dev-app-cluster")
                                        .nodegroupName("nodeG-dev-moni-node-containerd")
                                        .nodeRole("arn:aws:iam::100020003000:role/eks-cluster-workernode-role")
                                        .subnets("subnet-1000e2bf1ada4d5c0", "subnet-10005f077d31b5163")
                                        .amiType(AMITypes.CUSTOM)
                                        .capacityType(CapacityTypes.ON_DEMAND)
                                        .releaseVersion("ami-0d3944e04ba41ad0e")
                                        .launchTemplate(LaunchTemplateSpecification.builder()
                                                .name("eks-mon-workernode-containerd")
                                                .version("1")
                                                .build())
                                        .scalingConfig(NodegroupScalingConfig.builder()
                                                .desiredSize(1).maxSize(1).minSize(1)
                                                .build())
                                        .build())
                                .tag("Name", "testNodeGroup")
                                .build())
                        .tag("Name", "testCluster")
                        .build()
        );
        return awsEksClusters;
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void export() {
        Maps<Resource> export = exportEksClusters.export(client, null, null);
        log.debug("export => \n{}", export.unmarshall());
    }

    @Test
    @DisabledOnNoAwsCredentials
    public void listAWSEksClusters() {
        List<AWSEksCluster> awsEksClusters = exportEksClusters.listAWSEksClusters(client);
        log.debug("awsEksClusters => {}", awsEksClusters);
    }

    @Test
    public void getResourceMaps() {
        List<AWSEksCluster> awsEksClusters = getAwsEksClusters();

        Maps<Resource> resourceMaps = exportEksClusters.getResourceMaps(awsEksClusters);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/aws/expected/EksCluster.tf")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void getTFImport() {
        String expected = TestDataFileUtils.asString(resourceLoader.getResource("testData/aws/expected/EksCluster.cmd"));
        String actual = exportEksClusters.getTFImport(getAwsEksClusters()).script();

        assertEquals(expected, actual);
    }

}