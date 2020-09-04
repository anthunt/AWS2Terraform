package com.anthunt.terraform.generator.core.model;

import com.anthunt.terraform.generator.core.model.elements.*;
import lombok.Builder;

@Builder
public class Terraform extends AbstractMarshaller<Terraform> {

    private Maps<Variable> variables;
    private Locals locals;
    private Maps<Resource> resources;
    private Maps<Data> datas;
    private Maps<Provider> providers;
    private Maps<Output> outputs;

    @Override
    protected Terraform marshalling(String source) {
        throw new RuntimeException("NotImplemented");
    }

    @Override
    protected String unmarshalling(int tabSize) {
        return new StringBuffer()
                .append(providers.unmarshall(0))
                .append(locals.unmarshall(0))
                .append(variables.unmarshall(0))
                .append(datas.unmarshall(0))
                .append(resources.unmarshall(0))
                .append(outputs.unmarshall(0))
                .toString();
    }

}
