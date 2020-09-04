package com.anthunt.terraform.generator.core.model.elements;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Builder
public class Data extends AbstractMarshaller<Data> {

    private String api;
    private String name;
    private Arguments arguments;

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
                .append(arguments.unmarshall(tabSize))
                .append("}\n")
                .toString();
    }
}
