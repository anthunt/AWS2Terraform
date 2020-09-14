package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;

@Builder
public class TFBlock extends AbstractMarshaller<TFBlock> {

    private TFArguments arguments;

    @Override
    protected TFBlock marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return arguments == null ? "" : arguments.unmarshall(tabSize++);
    }
}
