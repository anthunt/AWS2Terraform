package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.ToString;

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
