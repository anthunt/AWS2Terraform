package com.anthunt.terraform.generator.core.model.terraform.nodes;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;

@Builder
public class Output extends AbstractMarshaller<Output> {

    private String name;
    private AbstractMarshaller<?> value;

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append("output ")
                .append(name)
                .append(" {\n")
                .append("\tvalue = ").append(value.unmarshall(tabSize++))
                .append("}\n\n")
                .toString();
    }
}
