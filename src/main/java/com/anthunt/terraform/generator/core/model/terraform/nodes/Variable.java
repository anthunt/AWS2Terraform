package com.anthunt.terraform.generator.core.model.terraform.nodes;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFType;
import lombok.Builder;

@Builder
public class Variable extends AbstractMarshaller<Variable> {

    private String name;
    private TFType type;
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
                .append(type != null ? "\t" + type.unmarshall(tabSize++) : "")
                .append(defaultValue != null ? "\tdefault = " + defaultValue.unmarshall(tabSize++) : "")
                .append("}\n\n")
                .toString();
    }
}
