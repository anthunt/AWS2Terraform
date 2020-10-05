package com.anthunt.terraform.generator.core.model.terraform.nodes;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class Locals extends AbstractMarshaller<Locals> {

    @Singular private Map<String, AbstractMarshaller<?>> locals;

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();

        int nextTabSize = tabSize + 1;

        stringBuffer.append("locals {\n");
        this.locals.forEach((key, value) -> {
            stringBuffer
                    .append("\t")
                    .append(key)
                    .append(" = ")
                    .append(value.unmarshall(nextTabSize));
        });
        stringBuffer.append("}\n\n");

        return stringBuffer.toString();
    }
}
