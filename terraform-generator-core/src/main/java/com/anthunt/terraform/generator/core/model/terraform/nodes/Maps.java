package com.anthunt.terraform.generator.core.model.terraform.nodes;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public class Maps<T extends AbstractMarshaller<T>> extends AbstractMarshaller<Maps> {

    @Singular private List<AbstractMarshaller<T>> maps;

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();
        this.maps.forEach((value) -> stringBuffer.append(value.unmarshall(tabSize)));

        return stringBuffer.toString();
    }

    public boolean isEmpty() {
        return this.maps.isEmpty();
    }

}
