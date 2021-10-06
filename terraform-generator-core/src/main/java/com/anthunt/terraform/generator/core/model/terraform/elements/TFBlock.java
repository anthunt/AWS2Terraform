package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.ToString;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@ToString
public class TFBlock extends AbstractMarshaller<TFBlock> {

    private TFArguments arguments;

    TFBlock(TFArguments arguments) {
        this.arguments = arguments;
    }

    public static TFBlockBuilder builder() {
        return new TFBlockBuilder();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return arguments == null ? "" : arguments.unmarshall(tabSize);
    }

    public static class TFBlockBuilder {

        private TFArguments.TFArgumentsBuilder tfArgumentsBuilder;
        private TFArguments arguments;

        TFBlockBuilder() {
        }

        public TFBlockBuilder argumentIf(boolean condition, String argumentKey, AbstractMarshaller<?> argumentValue) {
            return argumentIf(() -> condition, argumentKey, argumentValue);
        }

        public TFBlockBuilder argumentIf(boolean condition, String argumentKey, Supplier<AbstractMarshaller<?>> argumentValueSupplier) {
            return this.argumentIf(() -> condition, argumentKey, argumentValueSupplier);
        }

        public TFBlockBuilder argumentIf(BooleanSupplier booleanSupplier, String argumentKey, Supplier<AbstractMarshaller<?>> argumentValueSupplier) {
            if (booleanSupplier.getAsBoolean()) {
                return this.argument(argumentKey, argumentValueSupplier.get());
            } else {
                return this;
            }
        }

        public TFBlockBuilder argumentIf(BooleanSupplier booleanSupplier, String argumentKey, AbstractMarshaller<?> argumentValue) {
            if (booleanSupplier.getAsBoolean()) {
                return this.argument(argumentKey, argumentValue);
            } else {
                return this;
            }
        }

        public TFBlockBuilder argumentsIf(boolean condition, String argumentKey, List<AbstractMarshaller<?>> argumentValues) {
            return argumentsIf(() -> condition, argumentKey, argumentValues);
        }

        public TFBlockBuilder argumentsIf(BooleanSupplier booleanSupplier, String argumentKey, List<AbstractMarshaller<?>> argumentValues) {
            int inx = 0;
            for (AbstractMarshaller<?> argumentValue : argumentValues) {
                this.argumentIf(booleanSupplier, argumentKey + "$" + inx, argumentValue);
                inx++;
            }
            return this;
        }

        public TFBlockBuilder argument(String argumentKey, AbstractMarshaller<?> argumentValue) {
            if (tfArgumentsBuilder == null) {
                tfArgumentsBuilder = TFArguments.builder();
            }
            tfArgumentsBuilder.argument(argumentKey, argumentValue);
            return this;
        }

        public TFBlockBuilder arguments(TFArguments arguments) {
            if (tfArgumentsBuilder == null) {
                tfArgumentsBuilder = TFArguments.builder();
            }
            arguments.getArguments().entrySet().stream()
                    .forEach(entry -> tfArgumentsBuilder.argument(entry.getKey(), entry.getValue()));
            return this;
        }

        public TFBlock build() {
            this.arguments = this.tfArgumentsBuilder.build();
            return new TFBlock(arguments);
        }

        public String toString() {
            return "TFBlock.TFBlockBuilder(tfArgumentsBuilder=" + this.tfArgumentsBuilder + ", arguments=" + this.arguments + ")";
        }
    }
}
