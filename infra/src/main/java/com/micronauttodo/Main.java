package com.micronauttodo;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.List;

public class Main {
    public final static String MODULE_APP = "app-lambda-java";
    public final static String MODULE_APP_GRAALVM = "app-lambda-graalvm";
    public final static String MODULE_FUNCTION_COGNITO_POST_CONFIRMATION = "function-cognito-post-confirmation";
    private final static String ROOT_PACKAGE = "com.micronauttodo";
    private final static String PROJECT_NAME = "mntodo";
    private final static String PROJECT_DOMAIN = "micronauttodo.com";

    public static void main(final String[] args) {
        App app = new App();

        Project project = new Project(PROJECT_NAME, PROJECT_DOMAIN,
                List.of(new Module(MODULE_APP, ROOT_PACKAGE),
                        new Module(MODULE_APP_GRAALVM, ROOT_PACKAGE),
                        new Module(MODULE_FUNCTION_COGNITO_POST_CONFIRMATION, ROOT_PACKAGE + ".cognitopostconfirmation")));

        new AppStack(project, app, project.getName() + "AppStack", StackProps.builder()
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                .build());
        app.synth();
    }
}