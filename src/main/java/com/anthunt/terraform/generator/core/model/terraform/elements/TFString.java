package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;

@Builder
public class TFString extends AbstractMarshaller<TFString> {

    @Builder.Default
    private boolean isMultiline = false;

    @Builder.Default
    private String multiline_marker = "EOF";

    private String value;

    public static TFString build(String value) {
        return TFString.builder()
                .value(value)
                .build();
    }

    @Override
    protected TFString marshalling(String source) {
        return TFString.builder()
                .value(source)
                .build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        this.value = "".equals(this.value) ? null : this.value;
        return new StringBuffer()
                .append(this.value == null ? "" : this.isMultiline ? "<<" + this.multiline_marker + "\n" : "\"")
                .append(this.value)
                .append(this.value == null ? "\n" : this.isMultiline ? "\n" + this.multiline_marker : "\"")
                .append("\n")
                .toString();
    }
}
