# AWS2Terraform

[![Gitpod](https://img.shields.io/badge/build-Gitpod-green.svg)](https://gitpod.io/#https://github.com/anthunt/AWS2Terraform)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5fdf04c61e1e4ec087c5778aab631114)](https://app.codacy.com/manual/anthunt01/AWS2Terraform?utm_source=github.com&utm_medium=referral&utm_content=anthunt/AWS2Terraform&utm_campaign=Badge_Grade_Dashboard)
![Java CI with Gradle](https://github.com/anthunt/AWS2Terraform/workflows/Java%20CI%20with%20Gradle/badge.svg)
--
[![Status Not yet](https://img.shields.io/badge/Status-NotYet-yellow.svg)](#)
[![Language Java](https://img.shields.io/badge/Language-Java-orange.svg)](#)
[![License](https://img.shields.io/badge/License-Apache%202-blue.svg)](LICENSE)

This project is intended to extract resources created in AWS as Terraform resources.

I implemented the CLI using Spring Shell project and JCommander library.

The goal of the current project is an extract program for Terraform resources, and will be developed in the future towards generating tfvar and tfstates according to standardized Terraform code.

## Run

```
java -jar terraform-generator-shell-[version].jar
```

## Usage of Commands

How to check Spring Shell CLI Builtin option.

[https://docs.spring.io/spring-shell/docs/2.0.1.BUILD-SNAPSHOT/reference/htmlsingle/#built-in-commands](https://docs.spring.io/spring-shell/docs/2.0.1.BUILD-SNAPSHOT/reference/htmlsingle/#built-in-commands)

```
export>help
```
```
AVAILABLE COMMANDS

Built-In Commands
        clear: Clear the shell screen.
        exit, quit: Exit the shell.
        help: Display help about available commands.
        history: Display or save the history of previously run commands
        script: Read and execute commands from a file.
        stacktrace: Display the full stacktrace of the last error.

Ec2Commands
        export-ec2instances: Export terraform resources of ec2 instances.

Vpc Commands
        export-internet-gateways: Export terraform resources of InternetGateways
        export-nat-gateways: Export terraform resources of NatGateways
        export-vpcs: Export terraform resources of vpcs
```

## Common Options


How to check command-specific options.

```
export><command> help
```

```
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
      terraform resources file name will be generate with name <resource file 
      name>.tf 
      Default: main.tf
    -S, --silence
      no stdout.
      Default: true
    help
      display usage informations.
```

## AWS Services supported

1. EC2 Instances
2. VPCs
3. Internet Gateways
4. NAT Gateways
5. ... It will be added gradually.
