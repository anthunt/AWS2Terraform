package com.anthunt.terraform.generator.core.model.terraform;

public interface TerraformSource {

    String getTerraformResourceName();

    String getResourceId();

    default String getResourceName() {
        return getResourceId();
    }
}
