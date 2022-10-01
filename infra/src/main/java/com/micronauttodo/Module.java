package com.micronauttodo;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Module {
    private final String name;

    private final String path;
    private final String packageName;

    public Module(String name, String packageName) {
        this(name, packageName, name);
    }

    public Module(String name, String packageName, String path) {
        this.name = name;
        this.packageName = packageName;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }
}
