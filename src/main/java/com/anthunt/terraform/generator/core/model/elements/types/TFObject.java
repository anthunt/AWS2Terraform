package com.anthunt.terraform.generator.core.model.elements.types;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class TFObject extends AbstractMarshaller<TFObject> {

    @Singular private Map<String, AbstractMarshaller<?>> members;

    @Override
    protected TFObject marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("{\n");

        members.forEach((key, value) -> {
            stringBuffer
                    .append(key)
                    .append(" = ")
                    .append(value.unmarshall(tabSize));
        });

        stringBuffer.append("}\n");

        return stringBuffer.toString();
    }

}
