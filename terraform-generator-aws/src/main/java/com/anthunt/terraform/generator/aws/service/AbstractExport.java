package com.anthunt.terraform.generator.aws.service;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.utils.IOUtils;
import com.anthunt.terraform.generator.core.model.terraform.Terraform;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Provider;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import com.anthunt.terraform.generator.core.model.terraform.types.ProviderType;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.regions.Region;

@Slf4j
public abstract class AbstractExport<T extends SdkClient> {

    private String profileName;
    private Region region;

    public void exportTerraform(Class<T> t, CommonArgs commonArgs) {
        this.exportTerraform(t, commonArgs, null);
    }

    public void exportTerraform(Class<T> t, CommonArgs commonArgs, ExtraArgs extraArgs) {
        this.profileName = commonArgs.getProfile();
        this.region = Region.of(commonArgs.getRegion());
        Maps<Provider> providers = this.exportProvider();
        Maps<Resource> resources = this.export(
                AmazonClients.builder()
                        .profileName(profileName)
                        .region(this.region)
                        .build().getClient(t), commonArgs, extraArgs);

        if(commonArgs.isDeleteOutputDirectory()) {
            IOUtils.emptyDir(commonArgs.getOutputDirPath());
        }

        if(commonArgs.isExplicit()) {
            Terraform provider = Terraform.builder()
                    .providers(providers)
                    .build();
            String providerString = provider.unmarshall();
            IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getProviderFileName(), providerString, commonArgs.isSilence());
            if(!commonArgs.isSilence()) {
                log.info("result=>'{}'", providerString);
            }

            if(!resources.isEmpty()) {
                Terraform resource = Terraform.builder()
                        .resources(resources)
                        .build();
                String resourceString = resource.unmarshall();
                IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getResourceFileName(), resourceString, commonArgs.isSilence());
                if (!commonArgs.isSilence()) {
                    log.info("result=>'{}'", resourceString);
                }
            }
        } else {
            Terraform terraform = Terraform.builder()
                    .providers(providers)
                    .resources(resources)
                    .build();
            String terraformString = terraform.unmarshall();
            IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getResourceFileName(), terraformString, commonArgs.isSilence());
            if(!commonArgs.isSilence()) {
                log.info("result=>'{}'", terraformString);
            }
        }

    }

    protected abstract Maps<Resource> export(T client, CommonArgs commonArgs, ExtraArgs extraArgs);

    private Maps<Provider> exportProvider() {
        return Maps.<Provider>builder()
                .map(
                        Provider.builder()
                                .providerType(ProviderType.AWS)
                                .arguments(
                                        TFArguments.builder()
                                                .argument("region", TFString.build(this.region.id()))
                                                .argument("profile", TFString.build(this.profileName))
                                                .build()
                                )
                                .build()
                )
                .build();
    }

}
