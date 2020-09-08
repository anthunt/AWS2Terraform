package com.anthunt.terraform.generator.core.model.terraform;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractMarshaller<T> {

    protected abstract T marshalling(String source);
    protected abstract String unmarshalling(int tabSize);

    public T marshall(String source) {
        return this.marshalling(source);
    }

    public String unmarshall() {
        return this.unmarshall(0);
    }

    public String unmarshall(int tabSize) {
        return this.unmarshalling(tabSize);
    }

}
