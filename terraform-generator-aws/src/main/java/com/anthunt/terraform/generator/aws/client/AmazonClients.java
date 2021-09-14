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
import software.amazon.awssdk.services.directconnect.DirectConnectClient;
import software.amazon.awssdk.services.directory.DirectoryClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.efs.EfsClient;
import software.amazon.awssdk.services.elasticache.ElastiCacheClient;
import software.amazon.awssdk.services.elasticloadbalancing.ElasticLoadBalancingClient;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.s3.S3Client;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonClients {

    private Region region;
    private String profileName;

    private ProfileCredentialsProvider getCredentialsProvider() {
        if (profileName == null) {
            return ProfileCredentialsProvider.create();
        } else {
            return ProfileCredentialsProvider.create(profileName);
        }
    }

    @PostConstruct
    void init() {
        log.debug("region => '{}'", region );
        log.debug("profileName => '{}'", profileName );
    }

    public <T extends SdkClient> T getClient(Class<T> clazz) {
        Method[] methods = this.getClass().getDeclaredMethods();
        for(Method method : methods) {
            if(clazz.isNestmateOf(method.getReturnType())) {
                try {
                    //noinspection unchecked
                    return (T) method.invoke(this, new Object[0]);
                } catch (IllegalAccessException e) {
                    return null;
                } catch (InvocationTargetException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public Ec2Client getEc2Client() {
        return Ec2Client.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public ElasticLoadBalancingClient getElasticLoadBalancingClient() {
        return ElasticLoadBalancingClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public ElasticLoadBalancingV2Client getElasticLoadBalancingV2Client() {
        return ElasticLoadBalancingV2Client.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public ElastiCacheClient getElastiCacheClient() {
        return ElastiCacheClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public RdsClient getRdsClient() {
        return RdsClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public KmsClient getKmsClient() {
        return KmsClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public AcmClient getAcmClient() {
        return AcmClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public S3Client getS3Client() {
        return S3Client.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public LambdaClient getLambdaClient() {
        return LambdaClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public ApiGatewayClient getApiGatewayClient() {
        return ApiGatewayClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public IamClient getIamClient() {
        return IamClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public DirectConnectClient getDirectConnectClient() {
        return DirectConnectClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public DirectoryClient getDirectoryClient() {
        return DirectoryClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public Route53Client getRoute53Client() {
        return Route53Client.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public EcrClient getEcrClient() {
        return EcrClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    public EfsClient getEfsClient() {
        return EfsClient.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }
}
