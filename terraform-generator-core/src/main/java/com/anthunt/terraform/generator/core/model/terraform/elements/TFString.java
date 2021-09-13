package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class TFString extends AbstractMarshaller<TFString> {

    @Builder.Default
    private boolean isMultiline = false;

    @Builder.Default
    private boolean isLineIndent = true;

    @Builder.Default
    private boolean isEmptyStringToNull = true;

    @Builder.Default
    private String multiline_marker = "EOF";

    private String value;

    public static TFString build(String value) {
        return TFString.builder()
                .value(value)
                .build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        String currentValue = this.value;
        if (isEmptyStringToNull) {
            currentValue = "".equals(this.value) ? null : this.value;
        }

        if (currentValue == null) {
            return new StringBuffer()
                    .append("null")
                    .append(this.isLineIndent ? "\n" : "")
                    .toString();
        } else {
            return new StringBuffer()
                    .append(this.isMultiline ? "<<" + this.multiline_marker + "\n" : "\"")
                    .append(this.value)
                    .append(this.isMultiline ? "\n" + this.multiline_marker : "\"")
                    .append(this.isLineIndent ? "\n" : "")
                    .toString();
        }
    }
}
