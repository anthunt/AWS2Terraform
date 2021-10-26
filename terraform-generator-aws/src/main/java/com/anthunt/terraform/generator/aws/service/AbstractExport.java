package com.anthunt.terraform.generator.aws.service;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.utils.IOUtils;
import com.anthunt.terraform.generator.core.model.terraform.Terraform;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.imports.TFImport;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Provider;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import com.anthunt.terraform.generator.core.model.terraform.types.ProviderType;
import com.beust.jcommander.JCommander;
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
        if(commonArgs.isHelp()) {
            new JCommander(new CommonArgs()).usage();
            return;
        }

        this.printProgressBar(commonArgs.isSilence(), 1);

        this.profileName = commonArgs.getProfile();
        this.region = Region.of(commonArgs.getRegion());

        this.printProgressBar(commonArgs.isSilence(), 10);

        Maps<Provider> providers = this.exportProvider();
        Maps<Resource> resources = this.export(
                AmazonClients.builder()
                        .profileName(profileName)
                        .region(this.region)
                        .build().getClient(t), commonArgs, extraArgs);

        this.printProgressBar(commonArgs.isSilence(), 50);

        TFImport tfImport = this.scriptImport(AmazonClients.builder()
                .profileName(profileName)
                .region(this.region)
                .build().getClient(t), commonArgs, extraArgs);

        this.printProgressBar(commonArgs.isSilence(), 80);

        if (commonArgs.isDeleteOutputDirectory()) {
            IOUtils.emptyDir(commonArgs.getOutputDirPath());
        }

        this.printProgressBar(commonArgs.isSilence(), 81);

        if (commonArgs.isExplicit()) {
            Terraform provider = Terraform.builder()
                    .providers(providers)
                    .build();
            String providerString = provider.unmarshall();
            IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getProviderFileName(), providerString, commonArgs.isSilence());
            if (!commonArgs.isSilence()) {
                log.info("result=>'{}'", providerString);
            }

            this.printProgressBar(commonArgs.isSilence(), 85);

            if (!resources.isEmpty()) {
                Terraform resource = Terraform.builder()
                        .resources(resources)
                        .build();
                String resourceString = resource.unmarshall();
                IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getResourceFileName(), resourceString, commonArgs.isSilence());
                if (!commonArgs.isSilence()) {
                    log.info("result=>'{}'", resourceString);
                }
            }

            this.printProgressBar(commonArgs.isSilence(), 90);

            if (!tfImport.isEmpty()) {
                String scriptString = tfImport.script();
                IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getImportFileName(), scriptString, commonArgs.isSilence());
                if (!commonArgs.isSilence()) {
                    log.info("result=>'{}'", scriptString);
                }
            }


        } else {
            Terraform terraform = Terraform.builder()
                    .providers(providers)
                    .resources(resources)
                    .build();
            String terraformString = terraform.unmarshall();
            IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getResourceFileName(), terraformString, commonArgs.isSilence());
            if (!commonArgs.isSilence()) {
                log.info("result=>'{}'", terraformString);
            }
        }

        this.printProgressBar(commonArgs.isSilence(), 100);


    }

    protected abstract Maps<Resource> export(T client, CommonArgs commonArgs, ExtraArgs extraArgs);

    protected abstract TFImport scriptImport(T client, CommonArgs commonArgs, ExtraArgs extraArgs);

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

    private void printProgressBar(boolean isUse, long currentPosition) {
        if(isUse) {
            System.out.print(this.progressBar(100, currentPosition, 0, 100));
            System.out.print("\r");
            try {
                Thread.sleep(100);
            } catch (InterruptedException skip) {}
            if(currentPosition == 100) {
                System.out.println("\n");
            }
        }
    }

    private String progressBar(int progressBarSize, long currentPosition, long startPositoin, long finishPosition) {
        String bar = "";
        int nPositions = progressBarSize;
        char pb = '-'; //'░';
        char stat = '#'; //'█';
        for (int p = 0; p < nPositions; p++) {
            bar += pb;
        }
        int ststus = (int) (100 * (currentPosition - startPositoin) / (finishPosition - startPositoin));
        int move = (nPositions * ststus) / 100;
        return "|" + bar.substring(0, move).replace(pb, stat) + bar.substring(move, bar.length()) + "|" + ststus + "%|";
    }

}
