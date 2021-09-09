package com.anthunt.terraform.generator.core.model.terraform.nodes;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Resource extends AbstractMarshaller<Resource> {

    private String api;
    private String name;
    private TFArguments arguments;

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append("resource ")
                .append(api)
                .append(" ")
                .append(name)
                .append(" {\n")
                .append(arguments.unmarshall(tabSize))
                .append("}\n\n")
                .toString();
    }
}
