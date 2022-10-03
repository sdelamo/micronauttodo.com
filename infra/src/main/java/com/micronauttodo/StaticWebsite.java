package com.micronauttodo;

public class StaticWebsite implements HasSubdomain {
    private final String subdomain;

    private final String defaultRootObject;


    public StaticWebsite(String subdomain) {
        this(subdomain, null);
    }

    public StaticWebsite(String subdomain,
                         String defaultRootObject) {
        this.subdomain = subdomain;
        this.defaultRootObject = defaultRootObject;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public String getDefaultRootObject() {
        return defaultRootObject;
    }
}
