package com.anthunt.terraform.generator.core.model.terraform.nodes;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import lombok.Builder;

@Builder
public class Data extends AbstractMarshaller<Data> {

    private String api;
    private String name;
    private TFArguments arguments;

    @Override
    protected Data marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append("data ")
                .append(api)
                .append(" ")
                .append(name)
                .append(" {\n")
                .append(arguments.unmarshall(tabSize++))
                .append("}\n\n")
                .toString();
    }
}
