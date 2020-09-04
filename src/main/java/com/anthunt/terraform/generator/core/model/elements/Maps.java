package com.anthunt.terraform.generator.core.model.elements;

import com.anthunt.terraform.generator.core.model.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.Terraform;
import com.anthunt.terraform.generator.core.model.elements.types.TFString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public class Maps<T extends AbstractMarshaller<T>> extends AbstractMarshaller<Maps> {

    @Singular private List<AbstractMarshaller<?>> maps;

    @Override
    protected Maps marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();

        this.maps.forEach((value) -> stringBuffer.append(value.unmarshall(tabSize)));

        return stringBuffer.toString();
    }

}
