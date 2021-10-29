package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.iam.model.AWSRole;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
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

    private static final String DEFAULT_OUTPUT_FILE_NAME = "IamRoles";

    @Override
    protected Maps<Resource> export(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRole> awsRoles = listAwsRoles(client);
        return getResourceMaps(awsRoles);
    }

    @Override
    protected TFImport scriptImport(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSRole> awsRoles = listAwsRoles(client);
        return getTFImport(awsRoles);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSRole> listAwsRoles(IamClient client) {
        ListRolesResponse listPoliciesResponse = client.listRoles();
        return listPoliciesResponse.roles().stream()
                .filter(role -> !role.arn().startsWith("arn:aws:iam::aws:role/"))
                .map(role -> AWSRole.builder().role(role).build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSRole> roles) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSRole awsRole : roles) {
            Role role = awsRole.getRole();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api(awsRole.getTerraformResourceName())
                            .name(awsRole.getResourceName())
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

    TFImport getTFImport(List<AWSRole> awsRoles) {
        return TFImport.builder()
                .importLines(awsRoles.stream()
                        .map(awsRole -> TFImportLine.builder()
                                .address(awsRole.getTerraformAddress())
                                .id(awsRole.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
