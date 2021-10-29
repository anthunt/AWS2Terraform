package com.anthunt.terraform.generator.aws.command.args;

import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class ExtraArgs {

    @Singular
    private Map<String, Object> args;

    public <T> T get(String key, Class<T> t) {
        //noinspection unchecked
        return ((T) args.get(key).getClass());
    }
}
