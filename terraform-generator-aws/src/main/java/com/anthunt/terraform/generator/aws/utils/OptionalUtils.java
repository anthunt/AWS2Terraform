package com.anthunt.terraform.generator.aws.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public class OptionalUtils {

    public static <T> Optional<T> getExceptionAsOptional(Supplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
