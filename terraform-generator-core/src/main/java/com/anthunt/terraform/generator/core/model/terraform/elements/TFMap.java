package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
public class TFMap extends AbstractMarshaller<TFMap> {

    @Singular private Map<String, AbstractMarshaller<?>> maps;

    private static final TFMap EMPTY = new TFMap(Map.of());

    public static TFMap build(Map<String, AbstractMarshaller<?>> maps) {
        return TFMap.builder().maps(maps).build();
    }

    public static TFMap empty() {
        return EMPTY;
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
