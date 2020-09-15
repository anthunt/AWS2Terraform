package com.anthunt.terraform.generator.aws.client;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.acm.AcmClient;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.directconnect.DirectConnectClient;
import software.amazon.awssdk.services.directory.DirectoryClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
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

@Slf4j
@Getter
@Setter
@Component
public class AmazonClients {

    @Value("${amazon-clients.region:#{null}}")
    private Region region;

    @Value("${amazon-clients.profile-name:#{null}}")
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

    public static final class AmazonClientsBuilder {
        private Region region;
        private String profileName;

        private AmazonClientsBuilder() {
        }

        public static AmazonClientsBuilder builder() {
            return new AmazonClientsBuilder();
        }

        public AmazonClientsBuilder region(Region region) {
            this.region = region;
            return this;
        }

        public AmazonClientsBuilder profileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        public AmazonClients build() {
            AmazonClients amazonClients = new AmazonClients();
            amazonClients.region = this.region;
            amazonClients.profileName = this.profileName;
            return amazonClients;
        }
    }

}
