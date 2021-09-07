package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class TFExpression extends AbstractMarshaller<TFExpression> {

    private String expression;

    @Builder.Default
    private boolean isLineIndent = true;

    public static TFExpression build(String expression) {
        return TFExpression.builder()
                .expression(expression)
                .build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append(this.expression)
                .append(isLineIndent ? "\n" : "")
                .toString();
    }

}
