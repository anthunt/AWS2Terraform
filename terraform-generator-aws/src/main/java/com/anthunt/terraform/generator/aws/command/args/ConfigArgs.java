package com.anthunt.terraform.generator.aws.command.args;

import com.beust.jcommander.Parameter;
import lombok.*;
import software.amazon.awssdk.regions.Region;

@ToString
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigArgs {

    public static final String PROFILE_HELP = "aws profile name by ~/.aws/credentials and config ex) default";
    public static final String REGION_HELP = "aws region id ex) us-east-1";

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
