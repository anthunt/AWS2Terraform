package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;

@Builder
public class TFBool extends AbstractMarshaller<TFBool> {

    private Boolean bool;

    public static TFBool build(Boolean bool) {
        return TFBool.builder()
                .bool(bool)
                .build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append(this.bool == null ? null : this.bool.toString())
                .append("\n")
                .toString();
    }
}
