package com.anthunt.terraform.generator.core.model.elements;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class Locals extends AbstractMarshaller<Locals> {

    @Singular private Map<String, AbstractMarshaller<?>> locals;

    @Override
    protected Locals marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("locals {\n");
        this.locals.forEach((key, value) -> {
            stringBuffer
                    .append("\t")
                    .append(key)
                    .append(" = ")
                    .append(value.unmarshall(tabSize));
        });
        stringBuffer.append("}\n");

        return stringBuffer.toString();
    }
}
