package com.anthunt.terraform.generator.core.model.terraform;

import com.anthunt.terraform.generator.core.model.terraform.nodes.*;
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
                .append(this.providers == null ? "" : this.providers.unmarshall() + "\n")
                .append(this.variables == null ? "" : this.variables.unmarshall() + "\n")
                .append(this.locals == null ? "" : this.locals.unmarshall() + "\n")
                .append(this.datas == null ? "" : this.datas.unmarshall() + "\n")
                .append(this.resources == null ? "" : this.resources.unmarshall() + "\n")
                .append(this.outputs == null ? "" : this.outputs.unmarshall() + "\n")
                .toString();
    }

}
