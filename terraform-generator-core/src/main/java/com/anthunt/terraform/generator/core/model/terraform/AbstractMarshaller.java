package com.anthunt.terraform.generator.core.model.terraform;

public abstract class AbstractMarshaller<T> {

    protected abstract String unmarshalling(int tabSize);

    public String unmarshall() {
        return this.unmarshall(0);
    }

    public String unmarshall(int tabSize) {
        return this.unmarshalling(tabSize);
    }

}
