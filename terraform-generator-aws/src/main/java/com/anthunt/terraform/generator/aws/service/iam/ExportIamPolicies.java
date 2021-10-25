package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.iam.model.AWSPolicy;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.GetPolicyVersionRequest;
import software.amazon.awssdk.services.iam.model.ListPoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListPoliciesResponse;
import software.amazon.awssdk.services.iam.model.Policy;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportIamPolicies extends AbstractExport<IamClient> {

    @Override
    protected Maps<Resource> export(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSPolicy> awsPolicies = listAwsPolices(client);
        return getResourceMaps(awsPolicies);
    }

    @Override
    protected TFImport scriptImport(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSPolicy> awsPolicies = listAwsPolices(client);
        return getTFImport(awsPolicies);
    }

    List<AWSPolicy> listAwsPolices(IamClient client) {
        ListPoliciesResponse listPoliciesResponse = client.listPolicies(ListPoliciesRequest.builder().build());
        return listPoliciesResponse.policies().stream()
                .filter(policy -> !policy.arn().startsWith("arn:aws:iam::aws:policy/"))
                .map(policy -> AWSPolicy.builder().policy(policy)
                        .document(
                                decodeURL(
                                        client.getPolicyVersion(
                                                GetPolicyVersionRequest.builder()
                                                        .policyArn(policy.arn())
                                                        .versionId(policy.defaultVersionId())
                                                        .build()
                                        ).policyVersion().document()
                                )
                        ).build()
                )
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSPolicy> awsPolicies) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSPolicy awsPolicy : awsPolicies) {
            Policy policy = awsPolicy.getPolicy();
            String document = awsPolicy.getDocument();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api(awsPolicy.getTerraformResourceName())
                            .name(awsPolicy.getResourceName())
                            .argument("name", TFString.build(policy.policyName()))
                            .argument("path", TFString.build(policy.path()))
                            .argument("description", TFString.build(policy.description()))
                            .argument("policy", TFString.builder().isMultiline(true).value(document).build())
                            .build()
            );
        }
        return resourceMapsBuilder.build();
    }

    private String decodeURL(String origin) {
        return URLDecoder.decode(origin, StandardCharsets.UTF_8);
    }

    TFImport getTFImport(List<AWSPolicy> awsPolicies) {
        return TFImport.builder()
                .importLines(awsPolicies.stream()
                        .map(awsPolicy -> TFImportLine.builder()
                                .address(awsPolicy.getTerraformAddress())
                                .id(awsPolicy.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
