# AWS2Terraform ( AWS to Terraform )

[![Gitpod](https://img.shields.io/badge/build-Gitpod-green.svg)](https://gitpod.io/#https://github.com/anthunt/AWS2Terraform)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f952e93b7a7c4be7b60c625d3ef75cda)](https://www.codacy.com/gh/anthunt/AWS2Terraform/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=anthunt/AWS2Terraform&amp;utm_campaign=Badge_Grade)
[![gradle build](https://github.com/anthunt/AWS2Terraform/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/anthunt/AWS2Terraform/actions/workflows/gradle-build.yml)
[![Create Release](https://github.com/anthunt/AWS2Terraform/actions/workflows/create-release-with-changelog.yml/badge.svg)](https://github.com/anthunt/AWS2Terraform/actions/workflows/create-release-with-changelog.yml)
--
[![Status v0.1.0](https://img.shields.io/badge/Status-v0.1.0-green.svg)](#)
[![Language Java](https://img.shields.io/badge/Language-Java-orange.svg)](#)
[![License](https://img.shields.io/badge/License-Apache%202-blue.svg)](LICENSE)

This project is intended to extract resources created in AWS as Terraform resources.

I implemented the CLI using Spring Shell project and JCommander library.

The goal of the current project is an extract program for Terraform resources, and will be developed in the future towards generating tfvar and tfstates according to standardized Terraform code.

## AWS Services supported

1.  EC2 
  - EC2 Instances
  - Launch Templates
2.  VPCs
  - egress-only-internet-gateways
  - internet-gateways
  - nat-gateways
  - route-tables
  - security-groups
  - subnets
  - vpcs
3.  Api Gateway
  - api-gateway-resources
  - api-gateway-rest-apis
4.  Cloud Watch Logs
  - cloud-watch-log-groups
  - resource-policies
5.  ECR
  - ecr-repository
6.  EFS
  - efs-file-systems 
7.  EKS
  - eks-clusters
8.  ElastiCache
  - elasti-cache-clusters
  - elasti-cache-replication-groups
  - elasti-cache-subnet-groups
9.  Elastic Search(OpenSearch)
  - elastic-search-domains
10. ELB
  - load-balancer-listeners
  - load-balancer-target-groups
  - load-balancers
11. IAM
  - iam-instance-profiles
  - iam-policies
  - iam-role-policies
  - iam-role-policy-attachment
  - iam-roles
12. KMS
  - kms-keys
13. MSK
  - msk-clusters
14. RDS
  - rds-cluster-parameter-groups
  - rds-clusters
  - rds-option-groups
  - rds-subnet-groups
15. S3
  - s3buckets
16. It will be added gradually.

## Run

```shell
java -jar terraform-generator-shell-[version].jar
```


## Getting started
1.  configure region and profile.

```shell
export:>config --region ap-northeast-2 --profile default
Region is set to ap-northeast-2
Profile is set to default
```

2.  Use `all-available-resources` command to export all available-resources from aws.

```shell
export:>all-available-resources
ApiGatewayCommands : api-gateway-resources
|####################################################################################################|100%|

ApiGatewayCommands : api-gateway-rest-apis
|####################################################################################################|100%|

CloudWatchLogsCommands : cloud-watch-log-groups
|##########------------------------------------------------------------------------------------------|10%|
```

3.  Exit shell and check out result files which is created on outout folder.
*.tf files are terraform files, and *.cmd files are terraform import files.

```shell
export:>exit

$ cd output

$ ls
ApiGatewayResources.cmd           ElasticsearchDomains.cmd      InternetGateways.cmd           RdsSubnetGroups.cmd
ApiGatewayResources.tf            ElasticsearchDomains.tf       InternetGateways.tf            RdsSubnetGroups.tf
ApiGatewayRestApis.cmd            IamInstanceProfiles.cmd       KmsKeys.cmd                    RouteTables.cmd
ApiGatewayRestApis.tf             IamInstanceProfiles.tf        KmsKeys.tf                     RouteTables.tf
CloudWatchLogGroups.cmd           IamPolicies.cmd               LaunchTemplates.cmd            S3Buckets.cmd
CloudWatchLogGroups.tf            IamPolicies.tf                LaunchTemplates.tf             S3Buckets.tf
EcrRepository.cmd                 IamRolePolicies.cmd           MskClusters.cmd                SecurityGroups.cmd
EcrRepository.tf                  IamRolePolicies.tf            MskClusters.tf                 SecurityGroups.tf
EksClusters.cmd                   IamRolePolicyAttachments.cmd  NatGateways.cmd                Subnets.cmd
EksClusters.tf                    IamRolePolicyAttachments.tf   NatGateways.tf                 Subnets.tf
ElastiCacheReplicationGroups.cmd  IamRoles.cmd                  RdsClusterParameterGroups.cmd  Vpcs.cmd
ElastiCacheReplicationGroups.tf   IamRoles.tf                   RdsClusterParameterGroups.tf   Vpcs.tf
ElastiCacheSubnetGroups.cmd       Instances.cmd                 RdsClusters.cmd                provider.tf
ElastiCacheSubnetGroups.tf        Instances.tf                  RdsClusters.tf
```

## Usage of Commands

How to check Spring Shell CLI Builtin option.

[https://docs.spring.io/spring-shell/docs/2.0.1.BUILD-SNAPSHOT/reference/htmlsingle/#built-in-commands](https://docs.spring.io/spring-shell/docs/2.0.1.BUILD-SNAPSHOT/reference/htmlsingle/#built-in-commands)

```shell
export:>help
```

```shell
AVAILABLE COMMANDS

Api Gateway Commands
        api-gateway-resources: Export terraform resources of ApiGateway Resources.
        api-gateway-rest-apis: Export terraform resources of ApiGateway RestApis.

Built-In Commands
        clear: Clear the shell screen.
        exit, quit: Exit the shell.
        help: Display help about available commands.
        history: Display or save the history of previously run commands
        script: Read and execute commands from a file.
        stacktrace: Display the full stacktrace of the last error.

Cloud Watch Logs Commands
        cloud-watch-log-groups: Export terraform resources of CloudWatch LogGroups.
        resource-policies: Export terraform resources of CloudWatch Logs Resource Policies.

Config Commands
        config: Configure AWS2Terraform profile and region

Ec2Commands
        ec2instances: Export terraform resources of ec2 instances.
        launch-templates: Export terraform resources of launch templates.

Ecr Commands
        ecr-repository: Export terraform resources of ECR Repository.

Efs Commands
        efs-file-systems: Export terraform resources of ECR Repository.

Eks Commands
        eks-clusters: Export terraform resources of ec2 instances.

Elasti Cache Commands
        elasti-cache-clusters: Export terraform resources of elastiCache clusters.
        elasti-cache-replication-groups: Export terraform resources of elastiCache replication groups.
        elasti-cache-subnet-groups: Export terraform resources of elastiCache subnet groups.

Elastic Search Commands
        elastic-search-domains: Export terraform resources of Elasticsearch Domains.

Elb Commands
        load-balancer-listeners: Export terraform resources of LoadBalancerListeners.
        load-balancer-target-groups: Export terraform resources of LoadBalancerTargetGroups.
        load-balancers: Export terraform resources of LoadBalancers.

Export Multi Commands
        all-available-resources: Export terraform resources of ec2 instances.

Iam Commands
        iam-instance-profiles: Export terraform resources of iamInstanceProfiles.
        iam-policies: Export terraform resources of iamPolicies.
        iam-role-policies: Export terraform resources of iamRolePolicies.
        iam-role-policy-attachment: Export terraform resources of iamRolePolicyAttachment.
        iam-roles: Export terraform resources of iamRoles.

Kms Commands
        kms-keys: Export terraform resources of kms keys.

Msk Commands
        msk-clusters: Export terraform resources of MSK Clusters.

Rds Commands
        rds-cluster-parameter-groups: Export terraform resources of rds clusters parameter groups.
        rds-clusters: Export terraform resources of rds clusters.
        rds-option-groups: Export terraform resources of rds option groups.
        rds-subnet-groups: Export terraform resources of rds subnet groups.

S3Commands
        s3buckets: Export terraform resources of s3 buckets.

Vpc Commands
        egress-only-internet-gateways: Export terraform resources of EgressOnlyInternetGateways
        internet-gateways: Export terraform resources of InternetGateways
        nat-gateways: Export terraform resources of NatGateways
        route-tables: Export terraform resources of RouteTables
        security-groups: Export terraform resources of SecurityGroups
        subnets: Export terraform resources of Subnets
        vpcs: Export terraform resources of vpcs
```

## Config Commands
You can configure AWS2Terraform profile and region via config commands.
After that, you do not need to put common parameters when you run each command.
Auto completion is available for parameter of region and profile.

```shell
# without paramter options, you can check current configuration.
export:>config
Region=null, Profile=null

export:>config --region ap-northeast-2 --profile default
Region is set to ap-northeast-2
Profile is set to default

# you can omit parameter option name with '--'
export:>config ap-northeast-2 default
Region is set to ap-northeast-2
Profile is set to default
```

## Common Options


How to check command-specific options.

```shell
export:><command> help
```

```shell
Usage: <command> [options]
  Options:
    -D, --delete-output-directory
      delete output directory before generate.
      Default: true
    --dir
      output terraform file directory path
      Default: ./output
    -E, --explicit
      explicit output files by terraform types. - default: true
      Default: true
  * -P, --profile
      aws profile name by ~/.aws/credentials and config ex) default
    --provider-file-name
      provider.tf will be generate with name <provider file name>.tf
      Default: provider.tf
  * -R, --region
      aws region id ex) us-east-1
    --resource-file-name
      terraform resources file name will be generate with name <resource file name>.tf 
      Default: main.tf
    -S, --silence
      no stdout.
      Default: true
    help
      display usage informations.
```
