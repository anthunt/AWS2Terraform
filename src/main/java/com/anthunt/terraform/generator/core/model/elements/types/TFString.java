package com.anthunt.terraform.generator.core.model.elements.types;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
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
        return new StringBuffer()
                .append("\"")
                .append(this.value)
                .append("\"\n")
                .toString();
    }
}
