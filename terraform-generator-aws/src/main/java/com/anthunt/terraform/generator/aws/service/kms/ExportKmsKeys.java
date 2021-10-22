package com.anthunt.terraform.generator.aws.service.kms;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.kms.model.AWSKmsKey;
import com.anthunt.terraform.generator.aws.service.kms.model.AWSKmsKeyPolicy;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFNumber;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportKmsKeys extends AbstractExport<KmsClient> {

    @Override
    protected Maps<Resource> export(KmsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSKmsKey> awsKmsKeys = listKeys(client);
        return getResourceMaps(awsKmsKeys);
    }

    @Override
    protected TFImport scriptImport(KmsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSKmsKey> awsKmsKeys = listKeys(client);
        return getTFImport(awsKmsKeys);
    }

    List<AWSKmsKey> listKeys(KmsClient client) {
        ListKeysResponse listKeysResponse = client.listKeys();
        return listKeysResponse.keys().stream()
                .map(key -> AWSKmsKey.builder()
                        .keyMetadata(client.describeKey(DescribeKeyRequest.builder()
                                        .keyId(key.keyId())
                                        .build())
                                .keyMetadata())
                        .awsKeyPolicies(client.listKeyPolicies(ListKeyPoliciesRequest.builder()
                                        .keyId(key.keyId())
                                        .build())
                                .policyNames().stream()
                                .map(policyName -> AWSKmsKeyPolicy.builder()
                                        .name(policyName)
                                        .policy(client.getKeyPolicy(builder -> builder.keyId(key.keyId())
                                                        .policyName(policyName))
                                                .policy())
                                        .build())
                                .collect(Collectors.toList())
                        )
                        .aliases(client.listAliases(ListAliasesRequest.builder()
                                        .keyId(key.keyId())
                                        .build())
                                .aliases())
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSKmsKey> awsKmsKeys) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSKmsKey awsKmsKey : awsKmsKeys) {
            KeyMetadata keyMetadata = awsKmsKey.getKeyMetadata();
            List<AWSKmsKeyPolicy> awsKeyPolicies = awsKmsKey.getAwsKeyPolicies();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_kms_key")
                            .name(getResourceName(keyMetadata.keyId()))
                            .argument("description", TFString.build(keyMetadata.description()))
                            .argument("key_usage", TFString.build(keyMetadata.keyUsageAsString()))
                            .argument("deletion_window_in_days", TFNumber.builder()
                                    .value(Optional.ofNullable(keyMetadata.pendingDeletionWindowInDays())
                                            .map(Object::toString)
                                            .orElse(null))
                                    .build())
                            .argument("customer_master_key_spec", TFString.build(keyMetadata.keySpecAsString()))
                            .argumentsIf(Optional.ofNullable(awsKeyPolicies).isPresent(),
                                    "policy",
                                    () -> awsKeyPolicies.stream()
                                            .map(awsKmsKeyPolicy ->
                                                    TFString.builder().isMultiline(true)
                                                            .value(JsonUtils.toPrettyFormat(
                                                                    URLDecoder.decode(awsKmsKeyPolicy.getPolicy(),
                                                                            StandardCharsets.UTF_8)))
                                                            .build()
                                            ).collect(Collectors.toList()))
                            .build()
            );

            awsKmsKey.getAliases().forEach(aliasListEntry ->
                    resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_kms_alias")
                                    .name(getKmsAliasResourceName(aliasListEntry.aliasName()))
                                    .argument("name", TFString.build(aliasListEntry.aliasName()))
                                    .argument("target_key_id", TFString.build(aliasListEntry.targetKeyId()))
                                    .build()
                    )
            );
        }
        return resourceMapsBuilder.build();
    }

    private String getKmsAliasResourceName(String aliasName) {
        return aliasName.startsWith("alias/") ? aliasName.split("/")[1] : aliasName;
    }

    private String getResourceName(String keyId) {
        return MessageFormat.format("key-{0}", keyId);
    }

    private String decodeURL(String origin) {
        return URLDecoder.decode(origin, StandardCharsets.UTF_8);
    }

    TFImport getTFImport(List<AWSKmsKey> awsKmsKeys) {
        TFImport.TFImportBuilder tfImportBuilder = TFImport.builder();

        awsKmsKeys.forEach(awsKmsKey -> {
                    tfImportBuilder.importLine(TFImportLine.builder()
                            .address(MessageFormat.format("{0}.{1}",
                                    "aws_kms_key",
                                    getResourceName(awsKmsKey.getKeyMetadata().keyId())))
                            .id(awsKmsKey.getKeyMetadata().keyId())
                            .build());

                    awsKmsKey.getAliases().forEach(alias ->
                            tfImportBuilder.importLine(TFImportLine.builder()
                                    .address(MessageFormat.format("{0}.{1}",
                                            "aws_kms_alias",
                                            getResourceName(getKmsAliasResourceName(alias.aliasName()))))
                                    .id(awsKmsKey.getKeyMetadata().keyId())
                                    .build())
                    );
                }
        );
        return tfImportBuilder.build();
    }
}