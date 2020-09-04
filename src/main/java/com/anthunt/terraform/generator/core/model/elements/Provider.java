package com.anthunt.terraform.generator.core.model.elements;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import lombok.Builder;

@Builder
public class Provider extends AbstractMarshaller<Provider> {

    private ProviderType providerType;
    private Arguments arguments;

    @Override
    protected Provider marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append("provider ")
                .append(this.providerType.provider())
                .append(" {\n")
                .append(this.arguments.unmarshall(tabSize))
                .append("}\n")
                .toString();
    }
}
