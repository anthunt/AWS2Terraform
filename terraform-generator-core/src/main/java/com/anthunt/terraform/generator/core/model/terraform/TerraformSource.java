package com.anthunt.terraform.generator.core.model.terraform;

import java.text.MessageFormat;

public interface TerraformSource {

    String getTerraformResourceName();

    default String getTerraformAddress() {
        return MessageFormat.format("{0}.{1}", this.getTerraformResourceName(), this.getResourceName());
    }

    String getResourceId();

    String getResourceName();
}
