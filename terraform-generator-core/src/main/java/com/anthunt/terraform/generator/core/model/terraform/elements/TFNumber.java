package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;

@Builder
public class TFNumber extends AbstractMarshaller<TFNumber> {

    private String value;

    public static TFNumber build(String value) {
        return TFNumber.builder()
                .value(value)
                .build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append(this.value)
                .append("\n")
                .toString();
    }
}
