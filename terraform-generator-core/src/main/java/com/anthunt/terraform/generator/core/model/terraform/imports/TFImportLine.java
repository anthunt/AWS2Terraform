package com.anthunt.terraform.generator.core.model.terraform.imports;

import lombok.Builder;
import lombok.ToString;

import java.text.MessageFormat;

@Builder
@ToString
public class TFImportLine {
    private String address;
    private String id;

    public String script() {
        return MessageFormat.format("terraform import {0} {1}", address, id);
    }
}
