package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@ToString
public class TFArguments extends AbstractMarshaller<TFArguments> {

    private Map<String, AbstractMarshaller<?>> arguments;

    TFArguments(Map<String, AbstractMarshaller<?>> arguments) {
        this.arguments = arguments;
    }

    public static TFArgumentsBuilder builder() {
        return new TFArgumentsBuilder();
    }

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
                        .append("\t".repeat(tabSize + 1))
                        .append(key.split("\\$")[0])
                        .append(" {\n")
                        .append(val.unmarshall(nextTabSize))
                        .append("\t".repeat(tabSize + 1))
                        .append("}\n");
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

        private ArrayList<String> arguments$key;
        private ArrayList<AbstractMarshaller<?>> arguments$value;

        TFArgumentsBuilder() {
        }

        public TFArgumentsBuilder argumentIf(boolean condition, String argumentKey, AbstractMarshaller<?> argumentValue) {
            return argumentIf(() -> condition, argumentKey, argumentValue);
        }

        public TFArgumentsBuilder argumentIf(boolean condition, String argumentKey, Supplier<AbstractMarshaller<?>> argumentValueSupplier) {
            return this.argumentIf(() -> condition, argumentKey, argumentValueSupplier);
        }

        public TFArgumentsBuilder argumentIf(BooleanSupplier booleanSupplier, String argumentKey, Supplier<AbstractMarshaller<?>> argumentValueSupplier) {
            if (booleanSupplier.getAsBoolean()) {
                return this.argument(argumentKey, argumentValueSupplier.get());
            } else {
                return this;
            }
        }

        public TFArgumentsBuilder argumentIf(BooleanSupplier booleanSupplier, String argumentKey, AbstractMarshaller<?> argumentValue) {
            if (booleanSupplier.getAsBoolean()) {
                return this.argument(argumentKey, argumentValue);
            } else {
                return this;
            }
        }

        public TFArgumentsBuilder argumentsIf(boolean condition, String argumentKey, List<AbstractMarshaller<?>> argumentValues) {
            return argumentsIf(() -> condition, argumentKey, argumentValues);
        }

        public TFArgumentsBuilder argumentsIf(BooleanSupplier booleanSupplier, String argumentKey, List<AbstractMarshaller<?>> argumentValues) {
            int inx = 0;
            for (AbstractMarshaller<?> argumentValue : argumentValues) {
                this.argumentIf(booleanSupplier, argumentKey + "$" + inx, argumentValue);
                inx++;
            }
            return this;
        }

        public TFArgumentsBuilder argument(String argumentKey, AbstractMarshaller<?> argumentValue) {
            if (this.arguments$key == null) {
                this.arguments$key = new ArrayList<String>();
                this.arguments$value = new ArrayList<AbstractMarshaller<?>>();
            }
            this.arguments$key.add(argumentKey);
            this.arguments$value.add(argumentValue);
            return this;
        }

        public TFArgumentsBuilder arguments(Map<? extends String, ? extends AbstractMarshaller<?>> arguments) {
            if (this.arguments$key == null) {
                this.arguments$key = new ArrayList<String>();
                this.arguments$value = new ArrayList<AbstractMarshaller<?>>();
            }
            for (final Map.Entry<? extends String, ? extends AbstractMarshaller<?>> $lombokEntry : arguments.entrySet()) {
                this.arguments$key.add($lombokEntry.getKey());
                this.arguments$value.add($lombokEntry.getValue());
            }
            return this;
        }

        public TFArgumentsBuilder clearArguments() {
            if (this.arguments$key != null) {
                this.arguments$key.clear();
                this.arguments$value.clear();
            }
            return this;
        }

        public TFArguments build() {
            Map<String, AbstractMarshaller<?>> arguments;
            switch (this.arguments$key == null ? 0 : this.arguments$key.size()) {
                case 0:
                    arguments = java.util.Collections.emptyMap();
                    break;
                case 1:
                    arguments = java.util.Collections.singletonMap(this.arguments$key.get(0), this.arguments$value.get(0));
                    break;
                default:
                    arguments = new java.util.LinkedHashMap<String, AbstractMarshaller<?>>(this.arguments$key.size() < 1073741824 ? 1 + this.arguments$key.size() + (this.arguments$key.size() - 3) / 3 : Integer.MAX_VALUE);
                    for (int $i = 0; $i < this.arguments$key.size(); $i++)
                        arguments.put(this.arguments$key.get($i), (AbstractMarshaller<?>) this.arguments$value.get($i));
                    arguments = java.util.Collections.unmodifiableMap(arguments);
                    break;
            }

            return new TFArguments(arguments);
        }

        public String toString() {
            return "TFArguments.TFArgumentsBuilder(arguments$key=" + this.arguments$key + ", arguments$value=" + this.arguments$value + ", arguments$key=" + this.arguments$key + ", arguments$value=" + this.arguments$value + ")";
        }
    }
}
