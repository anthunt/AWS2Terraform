package com.anthunt.terraform.generator.core.model.terraform.nodes;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import com.anthunt.terraform.generator.core.model.terraform.types.ProviderType;
import lombok.Builder;

@Builder
public class Provider extends AbstractMarshaller<Provider> {

    private ProviderType providerType;
    private TFArguments arguments;

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
                .append(this.arguments.unmarshall(tabSize++))
                .append("}\n")
                .toString();
    }
}
