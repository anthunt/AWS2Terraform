package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.iam.model.AWSInstanceProfile;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFExpression;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFList;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImportLine;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.InstanceProfile;
import software.amazon.awssdk.services.iam.model.ListInstanceProfilesResponse;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportIamInstanceProfiles extends AbstractExport<IamClient> {

    private static final String DEFAULT_OUTPUT_FILE_NAME = "IamInstanceProfiles";

    @Override
    protected Maps<Resource> export(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSInstanceProfile> awsInstanceProfiles = listInstanceProfiles(client);
        return getResourceMaps(awsInstanceProfiles);
    }

    @Override
    protected TFImport scriptImport(IamClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {
        List<AWSInstanceProfile> awsInstanceProfiles = listInstanceProfiles(client);
        return getTFImport(awsInstanceProfiles);
    }

    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }

    List<AWSInstanceProfile> listInstanceProfiles(IamClient client) {
        ListInstanceProfilesResponse listInstanceProfilesResponse = client.listInstanceProfiles();
        return listInstanceProfilesResponse.instanceProfiles().stream()
                .filter(instanceProfile -> !instanceProfile.arn().startsWith("arn:aws:iam::aws:instance-profile/"))
                .map(instanceProfile -> AWSInstanceProfile.builder()
                        .instanceProfile(instanceProfile)
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSInstanceProfile> awsInstanceProfiles) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        for (AWSInstanceProfile awsInstanceProfile : awsInstanceProfiles) {
            InstanceProfile instanceProfile = awsInstanceProfile.getInstanceProfile();
            resourceMapsBuilder.map(
                    Resource.builder()
                            .api("aws_iam_instance_profile")
                            .name(instanceProfile.instanceProfileName())
                            .argument("name", TFString.build(instanceProfile.instanceProfileName()))
                            .argument("role", TFList.build(instanceProfile.roles().stream()
                                    .map(role -> TFExpression.builder().isLineIndent(false)
                                            .expression(MessageFormat.format("aws_iam_role.{0}.name", role.roleName()))
                                            .build())
                                    .collect(Collectors.toList())))
                            .build());
        }
        return resourceMapsBuilder.build();
    }

    TFImport getTFImport(List<AWSInstanceProfile> awsInstanceProfiles) {
        return TFImport.builder()
                .importLines(awsInstanceProfiles.stream()
                        .map(awsInstanceProfile -> TFImportLine.builder()
                                .address(awsInstanceProfile.getTerraformAddress())
                                .id(awsInstanceProfile.getResourceId())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }
}
