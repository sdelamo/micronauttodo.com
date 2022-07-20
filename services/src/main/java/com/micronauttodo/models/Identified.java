package com.micronauttodo.models;

import io.micronaut.core.annotation.NonNull;

@FunctionalInterface
public interface Identified {
    @NonNull
    String getId();
}
