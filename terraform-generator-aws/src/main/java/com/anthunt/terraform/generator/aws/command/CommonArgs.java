package com.anthunt.terraform.generator.aws.command;

import com.beust.jcommander.Parameter;
import lombok.Data;

@Data
public class CommonArgs {

    private static final String DEFAULT_PROFILE = "default";
    private static final String PROFILE_HELP = "aws profile name by ~/.aws/credentials and config - default: " + DEFAULT_PROFILE;
    private static final String DEFAULT_REGION = "ap-northeast-2";
    private static final String REGION_HELP = "aws region id - default: " + DEFAULT_REGION;
    private static final boolean DEFAULT_EXPLICIT = true;
    private static final String EXPLICIT_HELP = "explicit output files by terraform types. - default: " + DEFAULT_EXPLICIT;

    @Parameter(names = {"-P", "--profile"}, description = CommonArgs.PROFILE_HELP)
    private String profile = CommonArgs.DEFAULT_PROFILE;

    @Parameter(names = {"-R", "--region"}, description = CommonArgs.REGION_HELP)
    private String region = CommonArgs.DEFAULT_REGION;

    @Parameter(names = {"-E", "--explicit"}, description = CommonArgs.EXPLICIT_HELP)
    private boolean isExplicit = CommonArgs.DEFAULT_EXPLICIT;

    @Parameter(names = {"--dir"}, description = "output terraform file directory path")
    private String outputDirPath = "./output";

    @Parameter(names = {"--provider-file-name"}, description = "provider.tf will be generate with name <provider file name>.tf")
    private String providerFileName = "provider.tf";

    @Parameter(names = {"--resource-file-name"}, description = "terraform resources file name will be generate with name <resource file name>.tf")
    private String resourceFileName = "main.tf";

    @Parameter(names = {"-S", "--silence"}, description = "no stdout.")
    private boolean isSilence = true;

    @Parameter(names = {"-D", "--delete-output-directory"}, description = "delete output directory before generate.")
    private boolean isDeleteOutputDirectory = true;

}
