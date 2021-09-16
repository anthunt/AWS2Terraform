package com.anthunt.terraform.generator.core.model.terraform.nodes;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFArguments;
import lombok.ToString;

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
