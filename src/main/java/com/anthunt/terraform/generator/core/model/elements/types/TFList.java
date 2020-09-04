package com.anthunt.terraform.generator.core.model.elements.types;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public class TFList extends AbstractMarshaller<TFList> {

    @Singular private List<AbstractMarshaller<?>> lists;

    @Override
    protected TFList marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("[\n");
        lists.forEach(o ->stringBuffer.append(o.unmarshall(tabSize)).append(","));
        stringBuffer.append("]\n");

        return stringBuffer.toString();
    }
}
