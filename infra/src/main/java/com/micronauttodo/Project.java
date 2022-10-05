package com.micronauttodo;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Project {
    private final String name;

    private final String domainName;

    private final Module app;

    private final Module websockets;

    private final Module auth;

    private final StaticWebsite assets;

    private final StaticWebsite openApi;

    private final StaticWebsite web;

    public Project(String name,
                   String domainName,
                   Module app,
                   Module websockets,
                   Module auth,
                   StaticWebsite assets,
                   StaticWebsite openApi,
                   StaticWebsite web) {
        this.name = name;
        this.domainName = domainName;
        this.app = app;
        this.websockets = websockets;
        this.auth = auth;
        this.assets = assets;
        this.openApi = openApi;
        this.web = web;
    }

    public String getName() {
        return name;
    }

    public String getDomainName() {
        return domainName;
    }

    public Module getApp() {
        return app;
    }

    public Module getWebsockets() {
        return websockets;
    }

    public StaticWebsite getAssets() {
        return assets;
    }

    public StaticWebsite getOpenApi() {
        return openApi;
    }

    public StaticWebsite getWeb() {
        return web;
    }

    public Module getAuth() {
        return auth;
    }
}
