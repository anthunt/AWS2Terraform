package com.anthunt.terraform.generator.aws.client;

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

@Component
public class AmazonClients {

    private Region region;
    private String profileName;

    public Ec2Client ec2Client;
    public ElasticLoadBalancingClient elasticLoadBalancingClient;
    public ElasticLoadBalancingV2Client elasticLoadBalancingV2Client;
    public ElastiCacheClient elastiCacheClient;
    public RdsClient rdsClient;
    public KmsClient kmsClient;
    public AcmClient acmClient;
    public S3Client s3Client;
    public LambdaClient lambdaClient;
    public ApiGatewayClient apiGatewayClient;
    public IamClient iamClient;
    public DirectConnectClient directConnectClient;
    public DirectoryClient directoryClient;
    public Route53Client route53Client;


    public AmazonClients() {
        ProfileCredentialsProvider provider = ProfileCredentialsProvider.create();
        if (profileName == null) {
            initial(ProfileCredentialsProvider.create());
        } else {
            initial(ProfileCredentialsProvider.create(profileName));
        }
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

    private void initial(ProfileCredentialsProvider profileCredentialsProvider) {

        this.ec2Client = Ec2Client.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.elastiCacheClient = ElastiCacheClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.elasticLoadBalancingClient = ElasticLoadBalancingClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.elasticLoadBalancingV2Client = ElasticLoadBalancingV2Client.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.rdsClient = RdsClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.kmsClient = KmsClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.acmClient = AcmClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.lambdaClient = LambdaClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.apiGatewayClient = ApiGatewayClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.iamClient = IamClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.directConnectClient = DirectConnectClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.directoryClient = DirectoryClient.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

        this.route53Client = Route53Client.builder()
                .region(region)
                .credentialsProvider(profileCredentialsProvider)
                .build();

    }
}
