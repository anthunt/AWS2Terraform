package com.anthunt.terraform.generator.aws.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigRegistry {

    private String profile = "default";
    private String region = "ap-northeast-2";
    private static ConfigRegistry configRegistry;

    private ConfigRegistry() {}

    public static ConfigRegistry getInstance() {
        if(ConfigRegistry.configRegistry == null) {
            ConfigRegistry.configRegistry = new ConfigRegistry();
        }
        return ConfigRegistry.configRegistry;
    }
}
