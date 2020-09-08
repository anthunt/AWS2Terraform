package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.terraform.types.VariableType;
import lombok.Builder;

@Builder
public class TFType extends AbstractMarshaller<TFType> {

    private VariableType type;

    @Override
    protected TFType marshalling(String source) {
        return null;
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return null;
    }
}
