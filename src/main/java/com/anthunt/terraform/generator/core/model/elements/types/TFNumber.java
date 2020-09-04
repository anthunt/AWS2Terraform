package com.anthunt.terraform.generator.core.model.elements.types;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import lombok.Builder;

@Builder
public class TFNumber extends AbstractMarshaller<TFNumber> {

    private String value;

    @Override
    protected TFNumber marshalling(String source) {
        return TFNumber.builder()
                .value(source)
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
