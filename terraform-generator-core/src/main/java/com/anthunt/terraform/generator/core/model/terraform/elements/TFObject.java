package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
public class TFObject extends AbstractMarshaller<TFObject> {

    @Singular private Map<String, AbstractMarshaller<?>> members;

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();
        int nextTabSize = tabSize + 1;
        stringBuffer.append("{\n");
        members.forEach((key, value) -> {
            stringBuffer
                    .append("\t".repeat(tabSize + 1))
                    .append(key)
                    .append(" = ")
                    .append(value.unmarshall(nextTabSize));
        });
        stringBuffer
                .append("\t".repeat(tabSize))
                .append("}\n");
        return stringBuffer.toString();
    }

}
