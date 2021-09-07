package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.terraform.types.VariableType;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class TFType extends AbstractMarshaller<TFType> {

    private VariableType type;

    @Override
    protected String unmarshalling(int tabSize) {
        return null;
    }
}
