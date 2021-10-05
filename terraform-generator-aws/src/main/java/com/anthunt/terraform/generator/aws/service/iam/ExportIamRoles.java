package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.ListRolesResponse;
import software.amazon.awssdk.services.iam.model.Role;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportIamRoles extends AbstractExport<IamClient> {

    @Override
    protected Maps<Resource> export(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<Role> roles = listRoles(client);

        return getResourceMaps(roles);

    }

    List<Role> listRoles(IamClient client) {
        ListRolesResponse listPoliciesResponse = client.listRoles();
        return listPoliciesResponse.roles().stream()
                .filter(role -> !role.arn().startsWith("arn:aws:iam::aws:role/"))
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<Role> roles) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (Role role : roles) {

            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_iam_role")
                            .name(role.roleName())
                            .argument("name", TFString.build(role.roleName()))
                            .argument("path", TFString.build(role.path()))
                            .argument("description", TFString.build(role.description()))
                            .argument("assume_role_policy", TFString.builder().isMultiline(true).value(
                                    JsonUtils.toPrettyFormat(URLDecoder.decode(role.assumeRolePolicyDocument(), StandardCharsets.UTF_8))
                            ).build())
                            .build()
            );
        }
        return resourceMapsBuilder.build();
    }

    private String decodeURL(String origin) {
        return URLDecoder.decode(origin, StandardCharsets.UTF_8);
    }
}
