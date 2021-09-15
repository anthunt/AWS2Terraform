package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.iam.model.RolePolicyAttachmentDto;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFExpression;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListRolesResponse;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportIamRolePolicyAttachment extends AbstractExport<IamClient> {

    @Override
    protected Maps<Resource> export(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<RolePolicyAttachmentDto> rolePolicyAttachmentDtos = getRolePolices(client);

        return getResourceMaps(rolePolicyAttachmentDtos);
    }

    List<RolePolicyAttachmentDto> getRolePolices(IamClient client) {
        ListRolesResponse listPoliciesResponse = client.listRoles();
        return listPoliciesResponse.roles().stream()
                .filter(role -> !role.arn().startsWith("arn:aws:iam::aws:role/"))
                .peek(role -> log.debug("roleName => {}", role.roleName()))
                .flatMap(
                        role -> client.listAttachedRolePolicies(ListAttachedRolePoliciesRequest.builder()
                                .roleName(role.roleName())
                                .build())
                                .attachedPolicies()
                                .stream()
                                .map(attachedPolicy -> RolePolicyAttachmentDto.builder()
                                        .roleName(role.roleName())
                                        .policyName(attachedPolicy.policyName())
                                        .build()
                                )
                                .peek(rolePolicyAttachment -> log.debug("rolePolicyAttachment => {}", rolePolicyAttachment))
                )
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<RolePolicyAttachmentDto> rolePolicyAttachmentDtos) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (RolePolicyAttachmentDto rolePolicyAttachmentDto : rolePolicyAttachmentDtos) {
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_iam_role_policy_attachment")
                            .name(MessageFormat.format("{0}-attach-{1}", rolePolicyAttachmentDto.getRoleName(), rolePolicyAttachmentDto.getPolicyName()))
                            .arguments(
                                    TFArguments.builder()
                                            .argument("role", TFExpression.build(
                                                    MessageFormat.format("aws_iam_role.{0}.name", rolePolicyAttachmentDto.getRoleName())))
                                            .argument("policy_arn", TFExpression.build(
                                                    MessageFormat.format("aws_iam_policy.{0}.arn", rolePolicyAttachmentDto.getPolicyName())))
                                            .build()
                            ).build()
            );
        }
        return resourceMapsBuilder.build();
    }
}
