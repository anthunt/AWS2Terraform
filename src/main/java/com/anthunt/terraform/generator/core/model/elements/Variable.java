package com.anthunt.terraform.generator.core.model.elements;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.elements.types.VARType;
import lombok.Builder;

@Builder
public class Variable extends AbstractMarshaller<Variable> {

    private String name;
    private VARType type;
    private AbstractMarshaller<?> defaultValue;

    @Override
    protected Variable marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append("variable ")
                .append(name)
                .append(" {\n")
                .append(type != null ? "\t\t" + type.unmarshall(tabSize) : "")
                .append(defaultValue != null ? "\t\t" + defaultValue.unmarshall(tabSize) : "")
                .append("}\n")
                .toString();
    }
}
