package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFExpression;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.GetRolePolicyRequest;
import software.amazon.awssdk.services.iam.model.GetRolePolicyResponse;
import software.amazon.awssdk.services.iam.model.ListRolePoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListRolesResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportIamRolePolicies extends AbstractExport<IamClient> {

    @Override
    protected Maps<Resource> export(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<GetRolePolicyResponse> rolePolicies = listRolePolices(client);
        return getResourceMaps(rolePolicies);
    }

    @Override
    protected TFImport scriptImport(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<GetRolePolicyResponse> rolePolicies = listRolePolices(client);
        return getTFImport(rolePolicies);
    }

    List<GetRolePolicyResponse> listRolePolices(IamClient client) {
        ListRolesResponse listPoliciesResponse = client.listRoles();
        return listPoliciesResponse.roles().stream()
                .filter(role -> !role.arn().startsWith("arn:aws:iam::aws:role/"))
                .peek(role -> log.debug("roleName => {}", role.roleName()))
                .flatMap(
                        role -> client.listRolePolicies(ListRolePoliciesRequest.builder()
                                        .roleName(role.roleName())
                                        .build())
                                .policyNames()
                                .stream()
                                .map(policyName -> client.getRolePolicy(
                                        GetRolePolicyRequest.builder()
                                                .roleName(role.roleName())
                                                .policyName(policyName)
                                                .build())
                                )
                                .peek(rolePolicy -> log.debug("rolePolicy => {}", rolePolicy))
                )
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<GetRolePolicyResponse> rolePolicies) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (GetRolePolicyResponse rolePolicy : rolePolicies) {
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_iam_role_policy")
                            .name(rolePolicy.policyName())
                            .argument("name", TFString.build(rolePolicy.policyName()))
                            .argument("role", TFExpression.build(
                                    MessageFormat.format("aws_iam_role.{0}.id", rolePolicy.roleName())))
                            .argument("policy", TFString.builder().isMultiline(true).value(
                                            JsonUtils.toPrettyFormat(URLDecoder.decode(rolePolicy.policyDocument(), StandardCharsets.UTF_8)))
                                    .build())
                            .build()
            );
        }
        return resourceMapsBuilder.build();
    }

    private String decodeURL(String origin) {
        return URLDecoder.decode(origin, StandardCharsets.UTF_8);
    }

    TFImport getTFImport(List<GetRolePolicyResponse> getRolePolicyResponses) {
        return TFImport.builder()
                .importLines(getRolePolicyResponses.stream()
                        .map(rolePolicyResponse -> TFImportLine.builder()
                                .address(MessageFormat.format("{0}.{1}",
                                        "aws_iam_role_policy",
                                        rolePolicyResponse.policyName()))
                                .id(MessageFormat.format("{0}:{1}",
                                        rolePolicyResponse.roleName(),
                                        rolePolicyResponse.policyName()))
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
