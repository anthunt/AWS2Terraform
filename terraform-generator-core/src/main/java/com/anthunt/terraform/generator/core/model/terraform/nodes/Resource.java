package com.anthunt.terraform.generator.core.model.terraform.nodes;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import lombok.ToString;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@ToString
public class Resource extends AbstractMarshaller<Resource> {

    private String api;
    private String name;
    private TFArguments arguments;

    Resource(String api, String name, TFArguments arguments) {
        this.api = api;
        this.name = name;
        this.arguments = arguments;
    }

    public static ResourceBuilder builder() {
        return new ResourceBuilder();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append("resource ")
                .append(api)
                .append(" ")
                .append(name)
                .append(" {\n")
                .append(arguments.unmarshall(tabSize))
                .append("}\n\n")
                .toString();
    }

    public static class ResourceBuilder {
        private String api;
        private String name;
        private TFArguments arguments;
        private TFArguments.TFArgumentsBuilder tfArgumentsBuilder;

        ResourceBuilder() {
        }

        public ResourceBuilder api(String api) {
            this.api = api;
            return this;
        }

        public ResourceBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ResourceBuilder argument(String argumentKey, AbstractMarshaller<?> argumentValue) {
            if (tfArgumentsBuilder == null) {
                tfArgumentsBuilder = TFArguments.builder();
            }
            tfArgumentsBuilder.argument(argumentKey, argumentValue);
            return this;
        }

        public ResourceBuilder argumentIf(boolean condition, String argumentKey, AbstractMarshaller<?> argumentValue) {
            return argumentIf(() -> condition, argumentKey, argumentValue);
        }

        public ResourceBuilder argumentIf(boolean condition, String argumentKey, Supplier<AbstractMarshaller<?>> argumentValueSupplier) {
            return argumentIf(() -> condition, argumentKey, argumentValueSupplier);
        }

        public ResourceBuilder argumentIf(BooleanSupplier booleanSupplier, String argumentKey, Supplier<AbstractMarshaller<?>> argumentValueSupplier) {
            if (booleanSupplier.getAsBoolean()) {
                return this.argument(argumentKey, argumentValueSupplier.get());
            } else {
                return this;
            }
        }

        public ResourceBuilder argumentIf(BooleanSupplier booleanSupplier, String argumentKey, AbstractMarshaller<?> argumentValue) {
            if (booleanSupplier.getAsBoolean()) {
                return this.argument(argumentKey, argumentValue);
            } else {
                return this;
            }
        }

        public ResourceBuilder argumentsIf(boolean condition, String argumentKey, List<AbstractMarshaller<?>> argumentValues) {
            return argumentsIf(() -> condition, argumentKey, argumentValues);
        }

        public ResourceBuilder argumentsIf(boolean condition, String argumentKey, Supplier<List<AbstractMarshaller<?>>> argumentValuesSupplier) {
            if (condition) {
                return argumentsIf(() -> condition, argumentKey, argumentValuesSupplier.get());
            } else {
                return this;
            }
        }

        public ResourceBuilder argumentsIf(BooleanSupplier booleanSupplier, String argumentKey, List<AbstractMarshaller<?>> argumentValues) {
            int inx = 0;
            for (AbstractMarshaller<?> argumentValue : argumentValues) {
                this.argumentIf(booleanSupplier, argumentKey + "$" + inx, argumentValue);
                inx++;
            }
            return this;
        }

        public ResourceBuilder argumentsIf(BooleanSupplier booleanSupplier, String argumentKey, Supplier<List<AbstractMarshaller<?>>> argumentValuesSupplier) {
            if (booleanSupplier.getAsBoolean()) {
                return this.argumentsIf(booleanSupplier, argumentKey, argumentValuesSupplier.get());
            } else {
                return this;
            }
        }

        public ResourceBuilder arguments(TFArguments arguments) {
            if (tfArgumentsBuilder == null) {
                tfArgumentsBuilder = TFArguments.builder();
            }
            arguments.getArguments().entrySet().stream()
                    .forEach(entry -> tfArgumentsBuilder.argument(entry.getKey(), entry.getValue()));
            return this;
        }

        public Resource build() {
            this.arguments = this.tfArgumentsBuilder.build();
            return new Resource(api, name, arguments);
        }

        public String toString() {
            return "Resource.ResourceBuilder(api=" + this.api + ", name=" + this.name + ", arguments=" + this.arguments + ")";
        }
    }
}
