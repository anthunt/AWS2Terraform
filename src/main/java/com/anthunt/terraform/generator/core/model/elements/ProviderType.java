package com.anthunt.terraform.generator.core.model.elements;

public enum ProviderType {
    AWS("aws");

    private String provider;

    ProviderType(String provider) {
        this.provider = provider;
    }

    public String provider() {
        return this.provider;
    }
}
