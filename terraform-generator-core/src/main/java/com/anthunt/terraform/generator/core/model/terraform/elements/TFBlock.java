package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class TFBlock extends AbstractMarshaller<TFBlock> {

    private TFArguments arguments;

    @Override
    protected String unmarshalling(int tabSize) {
        return arguments == null ? "" : arguments.unmarshall(tabSize);
    }

    public static class TFBlockBuilder {

        private TFArguments.TFArgumentsBuilder tfArgumentsBuilder;

        public TFBlockBuilder argument(String argumentKey, AbstractMarshaller<?> argumentValue) {
            if (tfArgumentsBuilder == null) {
                tfArgumentsBuilder = TFArguments.builder();
            }
            tfArgumentsBuilder.argument(argumentKey, argumentValue);
            return this;
        }

        public TFBlock build() {
            this.arguments = this.tfArgumentsBuilder.build();
            return new TFBlock(arguments);
        }
    }
}
