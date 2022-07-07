package com.anthunt.terraform.generator.aws.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.acm.AcmClient;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.directconnect.DirectConnectClient;
import software.amazon.awssdk.services.directory.DirectoryClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.efs.EfsClient;
import software.amazon.awssdk.services.eks.EksClient;
import software.amazon.awssdk.services.elasticache.ElastiCacheClient;
import software.amazon.awssdk.services.elasticloadbalancing.ElasticLoadBalancingClient;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticsearch.ElasticsearchClient;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.kafka.KafkaClient;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.s3.S3Client;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class AmazonClients {

    private static Region region;
    private static String profileName;

    public static void setRegion(Region region) {
        AmazonClients.region = region;
    }

    public static void setProfileName(String profileName) {
        AmazonClients.profileName = profileName;
    }

    private static ProfileCredentialsProvider getCredentialsProvider() {
        if (profileName == null) {
            return ProfileCredentialsProvider.create();
        } else {
            return ProfileCredentialsProvider.create(profileName);
        }
    }

    @PostConstruct
    static void init() {
        log.debug("region => '{}'", region );
        log.debug("profileName => '{}'", profileName );
    }

    public static <T extends SdkClient> T getClient(Class<T> clazz) {
        Method[] methods = AmazonClients.class.getDeclaredMethods();
        for(Method method : methods) {

            if(clazz.getSimpleName().equals(method.getReturnType().getSimpleName())) {
                try {
                    //noinspection unchecked
                    return (T) method.invoke(null, new Object[0]);
                } catch (IllegalAccessException e) {
                    return null;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    public static Ec2Client getEc2Client() {
        return Ec2Client.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static ElasticLoadBalancingClient getElasticLoadBalancingClient() {
        return ElasticLoadBalancingClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static ElasticLoadBalancingV2Client getElasticLoadBalancingV2Client() {
        return ElasticLoadBalancingV2Client.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static ElastiCacheClient getElastiCacheClient() {
        return ElastiCacheClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static RdsClient getRdsClient() {
        return RdsClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static KmsClient getKmsClient() {
        return KmsClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static AcmClient getAcmClient() {
        return AcmClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static S3Client getS3Client() {
        return S3Client.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static LambdaClient getLambdaClient() {
        return LambdaClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static ApiGatewayClient getApiGatewayClient() {
        return ApiGatewayClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static IamClient getIamClient() {
        return IamClient.builder()
                .region(Region.AWS_GLOBAL)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static DirectConnectClient getDirectConnectClient() {
        return DirectConnectClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static DirectoryClient getDirectoryClient() {
        return DirectoryClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static Route53Client getRoute53Client() {
        return Route53Client.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static EcrClient getEcrClient() {
        return EcrClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static EksClient getEksClient() {
        return EksClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static EfsClient getEfsClient() {
        return EfsClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static KafkaClient getKafkaClient() {
        return KafkaClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static ElasticsearchClient getElasticsearchClient() {
        return ElasticsearchClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public static CloudWatchLogsClient getCloudWatchLogGroupClient() {
        return CloudWatchLogsClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }
}
