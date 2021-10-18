package com.anthunt.terraform.generator.core.model.terraform.imports;

import lombok.Builder;
import lombok.Singular;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@ToString
public class TFImport {
    @Singular
    private List<TFImportLine> importLines;

    public String script() {
        return importLines.stream().map(importLine -> importLine.script())
                .collect(Collectors.joining("\n"));
    }

    public boolean isEmpty() {
        return this.importLines.isEmpty();
    }
}
