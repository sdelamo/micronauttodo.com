package com.micronauttodo;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Module implements HasSubdomain {
    private final String name;

    private final String subdomain;

    private final String packageName;

    public Module(String name,
                  String subdomain,
                  String packageName) {
        this.name = name;
        this.subdomain = subdomain;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public String getPackageName() {
        return packageName;
    }
}
