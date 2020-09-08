package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
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
