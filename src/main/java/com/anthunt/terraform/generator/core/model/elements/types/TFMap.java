package com.anthunt.terraform.generator.core.model.elements.types;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class TFMap extends AbstractMarshaller<TFMap> {

    @Singular private Map<String, AbstractMarshaller<?>> maps;

    @Override
    protected TFMap marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("{\n");
        this.maps.forEach((key, value) -> {
            stringBuffer
                    .append(key)
                    .append(" = ")
                    .append(value.unmarshall(tabSize));
        });
        stringBuffer.append("}\n");

        return stringBuffer.toString();
    }
}
