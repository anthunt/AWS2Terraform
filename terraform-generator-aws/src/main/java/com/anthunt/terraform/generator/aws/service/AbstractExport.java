package com.anthunt.terraform.generator.aws.service;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.command.args.ExtraArgs;
import com.anthunt.terraform.generator.aws.config.ConfigRegistry;
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

import java.text.MessageFormat;
import java.util.Optional;

@Slf4j
public abstract class AbstractExport<T extends SdkClient> {

    private String profileName = "default'";
    private Region region;
    private long delayBetweenApis = 100;

    public long getDelayBetweenApis() {
        return delayBetweenApis;
    }

    public void setDelayBetweenApis(long delayBetweenApis) {
        this.delayBetweenApis = delayBetweenApis;
    }

    public void exportTerraform(Class<T> t, CommonArgs commonArgs) {
        this.exportTerraform(t, commonArgs, null);
    }

    public void exportTerraform(Class<T> t, CommonArgs commonArgs, ExtraArgs extraArgs) {
        if(commonArgs.isHelp()) {
            new JCommander(new CommonArgs()).usage();
            return;
        }

        this.printProgressBar(commonArgs.isSilence(), 1);

        if (Optional.ofNullable(commonArgs.getProfile()).isPresent()) {
            this.profileName = commonArgs.getProfile();
        } else {
            this.profileName = ConfigRegistry.getInstance().getProfile();
        }

        if (Optional.ofNullable(commonArgs.getRegion()).isPresent()) {
            this.region = Region.of(commonArgs.getRegion());
        } else {
            this.region = Region.of(ConfigRegistry.getInstance().getRegion());
        }
        String defaultOutputFileName = getDefaultOutputFileName();
        if (Optional.ofNullable(getDefaultOutputFileName()).isPresent()) {
            commonArgs.setResourceFileName(MessageFormat.format("{0}.tf", defaultOutputFileName));
            commonArgs.setImportFileName(MessageFormat.format("{0}.cmd", defaultOutputFileName));
        }

        this.printProgressBar(commonArgs.isSilence(), 10);

        ConfigRegistry.getInstance().setProfile(this.profileName);
        commonArgs.setProfile(this.profileName);
        AmazonClients.setRegion(this.region);
        AmazonClients.setProfileName(profileName);

        Maps<Provider> providers = this.exportProvider();
        Maps<Resource> resources = this.export(AmazonClients.getClient(t), commonArgs, extraArgs);

        this.printProgressBar(commonArgs.isSilence(), 50);

        TFImport tfImport = this.scriptImport(AmazonClients.getClient(t), commonArgs, extraArgs);

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

                this.printProgressBar(commonArgs.isSilence(), 90);

                if (!tfImport.isEmpty()) {
                    String scriptString = tfImport.script();
                    IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getImportFileName(), scriptString, commonArgs.isSilence());
                    if (!commonArgs.isSilence()) {
                        log.info("result=>'{}'", scriptString);
                    }
                }
            }
        } else {
            if (!resources.isEmpty()) {
                Terraform terraform = Terraform.builder()
                        .providers(providers)
                        .resources(resources)
                        .build();
                String terraformString = terraform.unmarshall();
                IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getResourceFileName(), terraformString, commonArgs.isSilence());
                if (!commonArgs.isSilence()) {
                    log.info("result=>'{}'", terraformString);
                }

                if (!tfImport.isEmpty()) {
                    String scriptString = tfImport.script();
                    IOUtils.writeFile(commonArgs.getOutputDirPath(), commonArgs.getImportFileName(), scriptString, commonArgs.isSilence());
                    if (!commonArgs.isSilence()) {
                        log.info("result=>'{}'", scriptString);
                    }
                }
            }
        }

        this.printProgressBar(commonArgs.isSilence(), 100);
        if (resources.isEmpty()) {
            System.out.println("No resource found!");
        }

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

    protected abstract String getDefaultOutputFileName();

    private void printProgressBar(boolean isUse, long currentPosition) {
        if(isUse) {
            System.out.print(this.progressBar(100, currentPosition, 0, 100));
            System.out.print("\r");
            try {
                Thread.sleep(100);
            } catch (InterruptedException skip) {
                skip.printStackTrace();
            }
            if(currentPosition == 100) {
                System.out.println("\n");
            }
        }
    }

    private String progressBar(int progressBarSize, long currentPosition, long startPositoin, long finishPosition) {
        StringBuilder bar = new StringBuilder();
        int nPositions = progressBarSize;
        char pb = '-'; //'░';
        char stat = '#'; //'█';
        bar.append(String.valueOf(pb).repeat(Math.max(0, nPositions)));
        int status = (int) (100 * (currentPosition - startPositoin) / (finishPosition - startPositoin));
        int move = (nPositions * status) / 100;
        return "|" + bar.substring(0, move).replace(pb, stat) + bar.substring(move, bar.length()) + "|" + status + "%|";
    }

}
