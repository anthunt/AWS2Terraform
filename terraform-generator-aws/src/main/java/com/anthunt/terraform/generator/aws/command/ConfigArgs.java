package com.anthunt.terraform.generator.aws.command;

import com.beust.jcommander.Parameter;
import lombok.ToString;
import software.amazon.awssdk.regions.Region;

@ToString
public class ConfigArgs {

    private static final String PROFILE_HELP = "aws profile name by ~/.aws/credentials and config ex) default";
    private static final String REGION_HELP = "aws region id ex) us-east-1";

    @Parameter(names = {"-P", "--profile"}, description = ConfigArgs.PROFILE_HELP)
    private String profile;

    @Parameter(names = {"-R", "--region"}, description = ConfigArgs.REGION_HELP)
    private String region;

    public String getProfile() {
        return this.profile;
    }

    public String getRegion() {
        return this.region;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setRegion(String region) {
        Region.of(region);
        this.region = region;
    }

    public boolean isNoArgs() {
        return profile == null && region == null;
    }
}
