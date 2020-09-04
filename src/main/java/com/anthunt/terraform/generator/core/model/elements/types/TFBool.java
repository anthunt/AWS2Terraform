package com.anthunt.terraform.generator.core.model.elements.types;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import lombok.Builder;

@Builder
public class TFBool extends AbstractMarshaller<TFBool> {

    private boolean bool;

    @Override
    protected TFBool marshalling(String source) {
        return TFBool.builder().bool(Boolean.parseBoolean(source)).build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append(this.bool)
                .append("\n")
                .toString();
    }
}
