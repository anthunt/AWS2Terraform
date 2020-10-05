package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public class TFList extends AbstractMarshaller<TFList> {

    @Singular private List<AbstractMarshaller<?>> lists;

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();
        int nextTabSize = tabSize + 1;
        stringBuffer.append("[\n");
        lists.forEach(o ->{
            stringBuffer
                    .append("\t".repeat(tabSize + 1))
                    .append(o.unmarshall(nextTabSize))
                    .append(lists.size() > 1 ? "\t".repeat(tabSize + 1) + ",\n" : "");
        });
        stringBuffer
                .append("\t".repeat(tabSize))
                .append("]\n");

        return stringBuffer.toString();
    }
}
