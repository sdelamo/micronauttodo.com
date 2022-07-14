package com.micronauttodo;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Module {
    private final String name;
    private final String packageName;

    public Module(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }
}
