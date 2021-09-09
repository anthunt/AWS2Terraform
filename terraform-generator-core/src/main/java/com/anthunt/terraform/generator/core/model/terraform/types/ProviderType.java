package com.anthunt.terraform.generator.core.model.terraform.types;

public enum ProviderType {
    AWS("aws");

    private final String provider;

    ProviderType(String provider) {
        this.provider = provider;
    }

    public String provider() {
        return this.provider;
    }
}
