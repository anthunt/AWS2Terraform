package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class TFNumber extends AbstractMarshaller<TFNumber> {

    private String value;

    @Builder.Default
    private boolean isLineIndent = true;

    public static TFNumber build(String value) {
        return TFNumber.builder()
                .value(value)
                .build();
    }

    public static TFNumber build(int value) {
        return TFNumber.builder()
                .value(value)
                .build();
    }

    public static TFNumber build(long value) {
        return TFNumber.builder()
                .value(value)
                .build();
    }

    public static TFNumber build(float value) {
        return TFNumber.builder()
                .value(value)
                .build();
    }

    public static TFNumber build(double value) {
        return TFNumber.builder()
                .value(value)
                .build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append(this.value)
                .append(isLineIndent ? "\n" : "")
                .toString();
    }

    public static class TFNumberBuilder {

        public TFNumberBuilder value(String value) {
            this.value = value;
            return this;
        }

        public TFNumberBuilder value(int value) {
            this.value = String.valueOf(value);
            return this;
        }

        public TFNumberBuilder value(long value) {
            this.value = String.valueOf(value);
            return this;
        }

        public TFNumberBuilder value(float value) {
            this.value = String.valueOf(value);
            return this;
        }

        public TFNumberBuilder value(double value) {
            this.value = String.valueOf(value);
            return this;
        }
    }
}
