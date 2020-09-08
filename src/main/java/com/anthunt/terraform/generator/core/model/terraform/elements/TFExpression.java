package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;

@Builder
public class TFExpression extends AbstractMarshaller<TFExpression> {

    private String expression;

    @Override
    protected TFExpression marshalling(String source) {
        return TFExpression.builder().expression(source).build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append(this.expression)
                .append("\n")
                .toString();
    }

}
