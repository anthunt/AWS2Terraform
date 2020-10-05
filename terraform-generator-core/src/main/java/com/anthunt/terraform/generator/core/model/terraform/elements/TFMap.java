package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class TFMap extends AbstractMarshaller<TFMap> {

    @Singular private Map<String, AbstractMarshaller<?>> maps;

    public static TFMap build(Map<String, AbstractMarshaller<?>> maps) {
        return TFMap.builder().maps(maps).build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();
        int nextTabSize = tabSize + 1;
        stringBuffer.append("{\n");
        this.maps.forEach((key, value) -> {
            stringBuffer
                    .append("\t".repeat(tabSize + 1))
                    .append("\"")
                    .append(key)
                    .append("\"")
                    .append(" = ")
                    .append(value.unmarshall(nextTabSize));
        });
        stringBuffer
                .append("\t".repeat(tabSize))
                .append("}\n");

        return stringBuffer.toString();
    }
}
