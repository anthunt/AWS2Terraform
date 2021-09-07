package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class TFNumber extends AbstractMarshaller<TFNumber> {

    private String value;

    @Builder.Default
    private boolean isLineIndent = true;

    public static TFNumber build(String value) {
        return TFNumber.builder()
                .value(value)
                .build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append(this.value)
                .append(isLineIndent ? "\n" : "")
                .toString();
    }
}
