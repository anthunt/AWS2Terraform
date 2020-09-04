package com.anthunt.terraform.generator.core.model.elements;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class Output extends AbstractMarshaller<Output> {

    private String name;
    private AbstractMarshaller<?> value;

    @Override
    protected Output marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append("output ")
                .append(name)
                .append(" {\n")
                .append("\tvalue = ").append(value.unmarshall(tabSize))
                .append("}\n")
                .toString();
    }
}
