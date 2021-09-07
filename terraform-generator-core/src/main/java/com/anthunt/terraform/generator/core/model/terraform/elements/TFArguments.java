package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
public class TFArguments extends AbstractMarshaller<TFArguments> {

    @Singular private Map<String, AbstractMarshaller<?>> arguments;

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();
        int nextTabSize = tabSize + 1;
        arguments.forEach((key, val) -> {
            if(val instanceof TFBlock) {
                stringBuffer
                        .append("\n")
                        .append("\t".repeat(tabSize + 1))
                        .append(key)
                        .append(" {\n")
                        .append(val.unmarshall(nextTabSize))
                        .append("\t".repeat(tabSize + 1))
                        .append("}\n\n");
            } else {
                stringBuffer
                        .append("\t".repeat(tabSize + 1))
                        .append(key)
                        .append(" = ")
                        .append(val.unmarshall(nextTabSize));
            }
        });
        return stringBuffer.toString();
    }

}
