package com.anthunt.terraform.generator.aws.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigRegistry {

    private String profile;
    private String region;
    private static ConfigRegistry configRegistry = new ConfigRegistry();

    private ConfigRegistry() {}

    public static ConfigRegistry getInstance() {
            return configRegistry;
    }
}
