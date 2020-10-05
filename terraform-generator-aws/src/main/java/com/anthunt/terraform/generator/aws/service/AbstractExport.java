package com.anthunt.terraform.generator.aws.service;

import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Provider;
import com.anthunt.terraform.generator.core.model.terraform.types.ProviderType;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.regions.Region;

public abstract class AbstractExport<T extends SdkClient> {

    private String profileName;
    private Region region;

    public void exportTerraform(String profileName, Region region, T client) {
        this.profileName = profileName;
        this.region = region;
        this.exportProvider();
        this.export(client);
    }

    protected abstract void export(T client);

    private void exportProvider() {
        Maps<Provider> providerMapsBuilder = Maps.<Provider>builder()
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
