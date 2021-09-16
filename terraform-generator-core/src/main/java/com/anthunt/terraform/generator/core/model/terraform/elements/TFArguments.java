package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Builder
@ToString
public class TFArguments extends AbstractMarshaller<TFArguments> {

    @Singular
    private Map<String, AbstractMarshaller<?>> arguments;

    public Map<String, AbstractMarshaller<?>> getArguments() {
        return arguments;
    }

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();
        int nextTabSize = tabSize + 1;
        arguments.forEach((key, val) -> {
            if (val instanceof TFBlock) {
                stringBuffer
                        .append("\n")
                        .append("\t".repeat(tabSize + 1))
                        .append(key.split("\\$")[0])
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

    public static class TFArgumentsBuilder {

        public TFArgumentsBuilder argumentIf(boolean condition, String argumentKey, AbstractMarshaller<?> argumentValue) {
            return argumentIf(() -> condition, argumentKey, argumentValue);
        }

        public TFArgumentsBuilder argumentIf(boolean condition, String argumentKey, List<AbstractMarshaller<?>> argumentValues) {
            return argumentIf(() -> condition, argumentKey, argumentValues);
        }

        public TFArgumentsBuilder argumentIf(BooleanSupplier booleanSupplier, String argumentKey, AbstractMarshaller<?> argumentValue) {
            if (booleanSupplier.getAsBoolean()) {
                return this.argument(argumentKey, argumentValue);
            } else {
                return this;
            }
        }

        public TFArgumentsBuilder argumentIf(BooleanSupplier booleanSupplier, String argumentKey, List<AbstractMarshaller<?>> argumentValues) {
            int inx = 0;
            for (AbstractMarshaller<?> argumentValue : argumentValues) {
                this.argumentIf(booleanSupplier, argumentKey + "$" + inx, argumentValue);
                inx++;
            }
            return this;
        }
    }
}
