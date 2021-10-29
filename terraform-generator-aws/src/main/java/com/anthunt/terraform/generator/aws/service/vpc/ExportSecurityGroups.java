package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.vpc.model.AWSSecurityGroup;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportSecurityGroups extends AbstractExport<Ec2Client> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "SecurityGroups";

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSSecurityGroup> awsSecurityGroups = listAwsSecurityGroups(client);
        return getResourceMaps(awsSecurityGroups);
    }

    @Override
    protected TFImport scriptImport(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSSecurityGroup> awsSecurityGroups = listAwsSecurityGroups(client);
        return getTFImport(awsSecurityGroups);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSSecurityGroup> listAwsSecurityGroups(Ec2Client client) {
        DescribeVpcsResponse describeVpcsResponse = client.describeVpcs();
        List<Vpc> vpcs = describeVpcsResponse.vpcs();

        List<AWSSecurityGroup> awsSecurityGroups = new ArrayList<>();
        for (Vpc vpc : vpcs) {

            List<Tag> names = vpc.tags().stream().filter(s -> "Name".equals(s.key())).collect(Collectors.toList());
            String vpcName = names.size() > 0 ? names.get(0).value().toLowerCase() : vpc.vpcId();
            log.debug("===============================================================");
            log.debug("vpcName : {}", vpcName);
            log.debug("===============================================================");

            DescribeSecurityGroupsResponse describeSecurityGroupResponse = client.describeSecurityGroups(
                    DescribeSecurityGroupsRequest.builder()
                            .filters(Filter.builder().name("vpc-id").values(vpc.vpcId()).build())
                            .build()
            );
            awsSecurityGroups.addAll(describeSecurityGroupResponse.securityGroups().stream()
                    .map(securityGroup -> AWSSecurityGroup.builder()
                            .securityGroup(securityGroup)
                            .build())
                    .collect(Collectors.toList()));
        }
        return awsSecurityGroups;
    }

    Maps<Resource> getResourceMaps(List<AWSSecurityGroup> awsSecurityGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        for (AWSSecurityGroup awsSecurityGroup : awsSecurityGroups) {
            SecurityGroup securityGroup = awsSecurityGroup.getSecurityGroup();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api(awsSecurityGroup.getTerraformResourceName())
                            .name(awsSecurityGroup.getResourceName())
                            .argument("name", TFString.build(securityGroup.groupName()))
                            .argument("description", TFString.build(securityGroup.description()))
                            .argument("vpc_id", TFString.build(securityGroup.vpcId()))
                            .argument("tags", TFMap.build(
                                    securityGroup.tags().stream()
                                            .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                            ))
                            .argument("ingress", TFList.builder()
                                    .lists(getRuleList(securityGroup.ipPermissions()))
                                    .build())
                            .argument("egress", TFList.builder()
                                    .lists(getRuleList(securityGroup.ipPermissionsEgress()))
                                    .build()
                            ).build()
            );
        }
        return resourceMapsBuilder.build();
    }

    private List<TFObject> getRuleList(List<IpPermission> ipPermissions) {
        List<TFObject> ruleList = new ArrayList<>();
        ruleList.addAll(
                ipPermissions.stream()
                        .flatMap(ipPermission -> {
                            log.debug("ipPermission => {}", ipPermission);
                            return ipPermission.userIdGroupPairs().stream().map(userIdGroupPair -> {
                                log.debug("userIdGroupPair => {}", userIdGroupPair);
                                return TFObject.builder()
                                        .member("description", TFString.builder().isEmptyStringToNull(false)
                                                .value(Optional.ofNullable(userIdGroupPair.description()).orElse("")).build())
                                        .member("from_port", TFNumber.builder()
                                                .value(Optional.ofNullable(ipPermission.fromPort()).orElse(0)).build())
                                        .member("to_port", TFNumber.builder()
                                                .value(Optional.ofNullable(ipPermission.toPort()).orElse(0)).build())
                                        .member("protocal", TFString.builder()
                                                .value(ipPermission.ipProtocol()).build())
                                        .member("cidr_blocks", TFList.builder()
                                                .lists(Collections.emptyList()).build())
                                        .member("ipv6_cidr_blocks", TFList.builder().isLineIndent(false)
                                                .lists(Collections.emptyList()).build())
                                        .member("security_groups", TFList.builder().isLineIndent(false)
                                                .lists(List.of(TFString.builder().isLineIndent(false).value(userIdGroupPair.groupId()).build()))
                                                .build())
                                        .build();
                            });
                        })
                        .collect(Collectors.toList()));

        ruleList.addAll(
                ipPermissions.stream()
                        .flatMap(ipPermission -> {
                            log.debug("ipPermission => {}", ipPermission);
                            return ipPermission.ipRanges().stream().map(ipRange -> {
                                log.debug("userIdGroupPair => {}", ipRange);
                                return TFObject.builder()
                                        .member("description", TFString.builder().isEmptyStringToNull(false)
                                                .value(Optional.ofNullable(ipRange.description()).orElse("")).build())
                                        .member("from_port", TFNumber.builder()
                                                .value(Optional.ofNullable(ipPermission.fromPort()).orElse(0).toString()).build())
                                        .member("to_port", TFNumber.builder()
                                                .value(Optional.ofNullable(ipPermission.toPort()).orElse(0).toString()).build())
                                        .member("protocal", TFString.builder()
                                                .value(ipPermission.ipProtocol()).build())
                                        .member("cidr_blocks", TFList.builder()
                                                .lists(ipPermission.ipRanges().stream()
                                                        .map(o1 -> TFString.builder().isLineIndent(false).value(o1.cidrIp()).build())
                                                        .collect(Collectors.toList())).build())
                                        .member("ipv6_cidr_blocks", TFList.builder().isLineIndent(false)
                                                .lists(Collections.emptyList()).build())
                                        .member("security_groups", TFList.builder().isLineIndent(false)
                                                .lists(Collections.emptyList()).build())
                                        .build();
                            });
                        })
                        .collect(Collectors.toList()));

        ruleList.addAll(
                ipPermissions.stream()
                        .flatMap(ipPermission -> {
                            log.debug("ipPermission => {}", ipPermission);
                            return ipPermission.ipv6Ranges().stream().map(ipRange -> {
                                log.debug("userIdGroupPair => {}", ipRange);
                                return TFObject.builder()
                                        .member("description", TFString.builder().isEmptyStringToNull(false)
                                                .value(Optional.ofNullable(ipRange.description()).orElse("")).build())
                                        .member("from_port", TFNumber.builder()
                                                .value(Optional.ofNullable(ipPermission.fromPort()).orElse(0)).build())
                                        .member("to_port", TFNumber.builder()
                                                .value(Optional.ofNullable(ipPermission.toPort()).orElse(0)).build())
                                        .member("protocal", TFString.builder()
                                                .value(ipPermission.ipProtocol()).build())
                                        .member("cidr_blocks", TFList.builder()
                                                .lists(Collections.emptyList()).build())
                                        .member("ipv6_cidr_blocks", TFList.builder().isLineIndent(false)
                                                .lists(ipPermission.ipv6Ranges().stream()
                                                        .map(o1 -> TFString.builder().isLineIndent(false).value(o1.cidrIpv6()).build())
                                                        .collect(Collectors.toList())).build())
                                        .member("security_groups", TFList.builder().isLineIndent(false)
                                                .lists(Collections.emptyList()).build())
                                        .build();
                            });
                        })
                        .collect(Collectors.toList()));
        return ruleList;
    }

    TFImport getTFImport(List<AWSSecurityGroup> awsSecurityGroups) {
        return TFImport.builder()
                .importLines(awsSecurityGroups.stream()
                        .map(awsSecurityGroup -> TFImportLine.builder()
                                .address(awsSecurityGroup.getTerraformAddress())
                                .id(awsSecurityGroup.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
