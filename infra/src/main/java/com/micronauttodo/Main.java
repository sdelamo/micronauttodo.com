package com.micronauttodo;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class Main {
    public static final String MODULE_WEBSOCKETS = "function-lambda-websockets";
    public static final String MODULE_APP = "app-lambda-java";
    public static final String MODULE_APP_GRAALVM = "app-lambda-graalvm";
    public static final String MODULE_FUNCTION_COGNITO_POST_CONFIRMATION = "function-cognito-post-confirmation";
    private static final String ROOT_PACKAGE = "com.micronauttodo";
    private static final String PROJECT_NAME = "mntodo";
    private static final String PROJECT_DOMAIN = null;
    public static final String SUBDOMAIN_WEBAPP = "webapp";
    public static final String SUBDOMAIN_AUTH = "auth";
    public static final String SUBDOMAIN_WEBSOCKET = "websocket";
    public static final String SUBDOMAIN_WEB = "web";
    public static final String SUBDOMAIN_OPENAPI = "openapi";
    public static final String SUBDOMAIN_ASSETS = "assets";

    public static void main(final String[] args) {
        App app = new App();
        Project project = new Project(PROJECT_NAME,
                PROJECT_DOMAIN,
                new Module(MODULE_APP, SUBDOMAIN_WEBAPP, ROOT_PACKAGE),
                //new GraalModule(MODULE_APP_GRAALVM, SUBDOMAIN_WEB, ROOT_PACKAGE),
                new Module(MODULE_WEBSOCKETS, SUBDOMAIN_WEBSOCKET, ROOT_PACKAGE + ".websockets.handler"),
                new Module(MODULE_FUNCTION_COGNITO_POST_CONFIRMATION, SUBDOMAIN_AUTH,ROOT_PACKAGE + ".cognitopostconfirmation"),
                new StaticWebsite(SUBDOMAIN_ASSETS, "index.html"),
                new StaticWebsite(SUBDOMAIN_OPENAPI, "micronaut-todo-1.0.yml"),
                new StaticWebsite(null, "index.html")
        );
        new AppStack(project, app, project.getName() + "AppStack", StackProps.builder()
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                .build());
        app.synth();
    }
}