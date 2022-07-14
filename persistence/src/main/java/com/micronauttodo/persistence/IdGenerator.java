package com.micronauttodo.persistence;

import io.micronaut.core.annotation.NonNull;

@FunctionalInterface
public interface IdGenerator {

    @NonNull
    String generate();
}
