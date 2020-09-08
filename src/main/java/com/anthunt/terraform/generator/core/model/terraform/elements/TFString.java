package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;

@Builder
public class TFString extends AbstractMarshaller<TFString> {

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
                .append(this.value == null ? "" : "\"")
                .append(this.value)
                .append(this.value == null ? "\n" : "\"\n")
                .toString();
    }
}
