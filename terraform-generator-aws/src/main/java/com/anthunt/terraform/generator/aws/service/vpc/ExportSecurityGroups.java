package com.anthunt.terraform.generator.aws.service.vpc;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.vpc.dto.SecurityGroupDto;
import com.anthunt.terraform.generator.core.model.terraform.elements.*;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportSecurityGroups extends AbstractExport<Ec2Client> {

    @Override
    protected Maps<Resource> export(Ec2Client client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<SecurityGroupDto> securityGroupDtos = getSecurityGroups(client);

        return getResourceMaps(securityGroupDtos);
    }


    List<SecurityGroupDto> getSecurityGroups(Ec2Client client) {
        DescribeVpcsResponse describeVpcsResponse = client.describeVpcs();
        List<Vpc> vpcs = describeVpcsResponse.vpcs();

        List<SecurityGroupDto> securityGroupDtos = new ArrayList<>();
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
            List<SecurityGroup> securityGroups = describeSecurityGroupResponse.securityGroups();
            for (SecurityGroup securityGroup : securityGroups) {
                SecurityGroupDto securityGroupDto = new SecurityGroupDto();
                securityGroupDto.setVpcName(vpcName);
                securityGroupDto.setSecurityGroup(securityGroup);
                securityGroupDtos.add(securityGroupDto);
            }
        }
        return securityGroupDtos;
    }

    Maps<Resource> getResourceMaps(List<SecurityGroupDto> securityGroupDtos) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();

        for (SecurityGroupDto securityGroupDto : securityGroupDtos) {
            log.debug("securityGroupDto => {}", securityGroupDto);
            SecurityGroup securityGroup = securityGroupDto.getSecurityGroup();
            resourceMapsBuilder.map(
                Resource.builder()
                    .api("security_groups")
                    .name(securityGroup.groupName())
                    .arguments(
                        TFArguments.builder()
                            .argument("vpc_id", TFString.build(securityGroup.vpcId()))
                            .argument("description", TFString.build(securityGroup.description()))
                            .argument("tags", TFMap.build(
                                securityGroup.tags().stream()
                                        .collect(Collectors.toMap(Tag::key, tag -> TFString.build(tag.value())))
                            ))
                            .argument("ingress", TFList.builder()
                                .lists(
                                    securityGroup.ipPermissions().stream()
                                        .flatMap(ipPermission -> {
                                            log.debug("ipPermission => {}", ipPermission);
                                            return ipPermission.userIdGroupPairs().stream().map(userIdGroupPair -> {
                                                log.debug("userIdGroupPair => {}", userIdGroupPair);
                                                return TFList.builder().isLineIndent(false)
                                                        .list(TFNumber.builder().isLineIndent(false).value(Optional.ofNullable(ipPermission.fromPort()).orElse(0).toString()).build())
                                                        .list(TFNumber.builder().isLineIndent(false).value(Optional.ofNullable(ipPermission.toPort()).orElse(0).toString()).build())
                                                        .list(TFString.builder().isLineIndent(false).value(ipPermission.ipProtocol()).build())
                                                        .list(TFExpression.builder().isLineIndent(false).expression("[\"" + userIdGroupPair.groupId() + "\"]").build())
                                                        .list(TFString.builder().isLineIndent(false).isEmptyStringToNull(false).value(Optional.ofNullable(userIdGroupPair.description()).orElse("")).build())
                                                        .list(TFBool.builder().isLineIndent(false).bool(false).build())
                                                        .build();
                                            });
                                        })
                                        .collect(Collectors.toList()))
                                .build())
                            .argument("egress", TFList.builder()
                                .lists(
                                    securityGroup.ipPermissionsEgress().stream()
                                        .flatMap(ipPermission -> {
                                            log.debug("ipPermission => {}", ipPermission);
                                            return ipPermission.userIdGroupPairs().stream().map(userIdGroupPair -> {
                                                log.debug("userIdGroupPair => {}", userIdGroupPair);
                                                return TFList.builder().isLineIndent(false)
                                                        .list(TFNumber.builder().isLineIndent(false).value(Optional.ofNullable(ipPermission.fromPort()).orElse(0).toString()).build())
                                                        .list(TFNumber.builder().isLineIndent(false).value(Optional.ofNullable(ipPermission.toPort()).orElse(0).toString()).build())
                                                        .list(TFString.builder().isLineIndent(false).value(ipPermission.ipProtocol()).build())
                                                        .list(TFExpression.builder().isLineIndent(false).expression("[\"" + userIdGroupPair.groupId() + "\"]").build())
                                                        .list(TFString.builder().isLineIndent(false).isEmptyStringToNull(false).value(Optional.ofNullable(userIdGroupPair.description()).orElse("")).build())
                                                        .list(TFBool.builder().isLineIndent(false).bool(false).build())
                                                        .build();
                                            });
                                        })
                                        .collect(Collectors.toList()))
                                .build()
                            ).build()
                    ).build()
            );
        }
        return resourceMapsBuilder.build();
    }
}
