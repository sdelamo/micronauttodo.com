package com.micronauttodo;

import io.micronaut.aws.cdk.function.MicronautFunction;
import io.micronaut.aws.cdk.function.MicronautFunctionFile;
import io.micronaut.core.util.StringUtils;
import io.micronaut.starter.application.ApplicationType;
import io.micronaut.starter.options.BuildTool;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.DomainNameOptions;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketRouteOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketStage;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.WebSocketLambdaIntegration;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.CertificateValidation;
import software.amazon.awscdk.services.cloudfront.Behavior;
import software.amazon.awscdk.services.cloudfront.CloudFrontWebDistribution;
import software.amazon.awscdk.services.cloudfront.OriginAccessIdentity;
import software.amazon.awscdk.services.cloudfront.S3OriginConfig;
import software.amazon.awscdk.services.cloudfront.SourceConfiguration;
import software.amazon.awscdk.services.cloudfront.ViewerCertificate;
import software.amazon.awscdk.services.cloudfront.ViewerCertificateOptions;
import software.amazon.awscdk.services.cloudfront.ViewerProtocolPolicy;
import software.amazon.awscdk.services.cognito.CustomDomainOptions;
import software.amazon.awscdk.services.cognito.OAuthFlows;
import software.amazon.awscdk.services.cognito.OAuthScope;
import software.amazon.awscdk.services.cognito.OAuthSettings;
import software.amazon.awscdk.services.cognito.SignInAliases;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.cognito.UserPoolClient;
import software.amazon.awscdk.services.cognito.UserPoolClientOptions;
import software.amazon.awscdk.services.cognito.UserPoolDomain;
import software.amazon.awscdk.services.cognito.UserPoolDomainOptions;
import software.amazon.awscdk.services.cognito.UserPoolOperation;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.GlobalSecondaryIndexProps;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.route53.ARecord;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProviderProps;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.amazon.awscdk.services.route53.RecordTarget;
import software.amazon.awscdk.services.route53.targets.ApiGateway;
import software.amazon.awscdk.services.route53.targets.CloudFrontTarget;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.ses.CfnEmailIdentity;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppStack extends Stack {

    private final static String OAUTH_CLIENT_NAME = "cognito";

    private final static String LOCALHOST = "http://localhost:8080";

    protected static final String ATTRIBUTE_PK = "pk";
    protected static final String ATTRIBUTE_SK = "sk";
    protected static final String ATTRIBUTE_GSI_1_PK = "GSI1PK";
    protected static final String ATTRIBUTE_GSI_1_SK = "GSI1SK";
    protected static final String INDEX_GSI_1 = "GSI1";
    protected static final String ATTRIBUTE_GSI_2_PK = "GSI2PK";
    protected static final String ATTRIBUTE_GSI_2_SK = "GSI2SK";
    protected static final String INDEX_GSI_2 = "GS22";
    protected static final String ATTRIBUTE_GSI_3_PK = "GSI3PK";
    protected static final String ATTRIBUTE_GSI_3_SK = "GSI3SK";
    protected static final String INDEX_GSI_3 = "GS23";
    private static final String HTTPS = "https://";

    private final Project project;

    public AppStack(final Project project, final Construct parent, final String id) {
        this(project, parent, id, null);
    }

    public AppStack(final Project project, final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);
        this.project = project;

        IHostedZone zone = findZone();
        Certificate cert = createCertificate(zone);

        Table table = createTable();
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("DYNAMODB_TABLE_NAME", table.getTableName());
        // https://aws.amazon.com/blogs/compute/optimizing-aws-lambda-function-performance-for-java/
        environmentVariables.put("JAVA_TOOL_OPTIONS", "-XX:+TieredCompilation -XX:TieredStopAtLevel=1");

        Module appModule = project.findModuleByName(Main.MODULE_APP);
        Function function = createAppFunction(environmentVariables, appModule.getName()).build();
        table.grantReadWriteData(function);

        Module appModuleNative = project.findModuleByName(Main.MODULE_APP_GRAALVM);
        Function functionNative = createAppFunction(environmentVariables, appModuleNative.getName(), true).build();
        table.grantReadWriteData(functionNative);

        LambdaRestApi api = createApi("webapp", functionNative, cert, zone);

        Module postConfirmationModule = project.findModuleByName(Main.MODULE_FUNCTION_COGNITO_POST_CONFIRMATION);
        Function postConfirmationFunction = createFunction(environmentVariables,
                postConfirmationModule.getName(),
                functionHandler(postConfirmationModule)
                ).build();
        table.grantReadWriteData(postConfirmationFunction);

        environmentVariables = createAuthorizationServer(cert, zone, postConfirmationFunction);
        addEnvironmentVariablesToFunction(environmentVariables, function);
        addEnvironmentVariablesToFunction(environmentVariables, functionNative);

        Bucket openApiBucket = createBucket(project.getName() + "-s3-openapi");
        createCloudFrontDistribution("openapi", cert, openApiBucket, zone, "micronaut-todo-1.0.yml");

        Bucket assetsBucket = createBucket(project.getName() + "-s3-assets");
        createCloudFrontDistribution("assets", cert, assetsBucket, zone, "index.html");

        Bucket webBucket = createBucket(project.getName() + "-s3-web");
        createCloudFrontDistribution(null, cert, webBucket, zone, "index.html");

        Module websocketsModule = project.findModuleByName(Main.MODULE_WEBSOCKETS);
        Function websocketsFunction = createFunction(environmentVariables,
                websocketsModule.getName(),
                functionHandler(websocketsModule)
        ).build();
        WebSocketApi webSocketApi = createWebSocketApi(project.getName(), websocketsFunction);
        webSocketApi.grantManageConnections(websocketsFunction);
        WebSocketStage stage = createWebSocketStage(project.getName(), webSocketApi);
        stage.grantManagementApiAccess(websocketsFunction);

        output(api);
    }

    private WebSocketApi createWebSocketApi(String projectName, Function function) {
        return WebSocketApi.Builder.create(this, projectName + "-function-api-websocket")
                .defaultRouteOptions(WebSocketRouteOptions.builder()
                        .integration((new WebSocketLambdaIntegration("default-route-integration", function)))
                        .build())
                .connectRouteOptions(WebSocketRouteOptions.builder()
                        .integration((new WebSocketLambdaIntegration("connect-route-integration", function)))
                        .build())
                .disconnectRouteOptions(WebSocketRouteOptions.builder()
                        .integration((new WebSocketLambdaIntegration("disconnect-route-integration", function)))
                        .build())
                .build();
    }

    private WebSocketStage createWebSocketStage(String projectName, WebSocketApi webSocketApi) {
        return WebSocketStage.Builder.create(this, projectName + "-function-api-websocket-stage-production")
                .webSocketApi(webSocketApi)
                .stageName("production")
                .autoDeploy(true)
                .build();
    }

    private Bucket createBucket(String id) {
        return Bucket.Builder.create(this, id).build();
    }

    private CloudFrontWebDistribution createCloudFrontDistribution(String subdomain,
                                                                   Certificate certificate,
                                                                   Bucket bucket,
                                                                   IHostedZone zone,
                                                                   String defaultRootObject) {
        String domainName = subdomain != null ?
                subdomain + "." + project.getDomainName() : project.getDomainName();
        CloudFrontWebDistribution cloudFrontWebDistribution = CloudFrontWebDistribution.Builder.create(this,
                project.getName() + "-" + subdomain  + "-cloudfront-distribution")
                .originConfigs(Collections.singletonList(SourceConfiguration.builder()
                        .s3OriginSource(S3OriginConfig.builder()
                                .s3BucketSource(bucket)
                                .originAccessIdentity(OriginAccessIdentity.Builder.create(this, project.getName() + "-origin-access-identity")
                                        .build())
                                .build())
                        .behaviors(Collections.singletonList(Behavior.builder()
                                .isDefaultBehavior(true)
                                .viewerProtocolPolicy(ViewerProtocolPolicy.REDIRECT_TO_HTTPS)
                                .build()))
                        .build()))
                .defaultRootObject(defaultRootObject)
                .viewerCertificate(ViewerCertificate.fromAcmCertificate(certificate, ViewerCertificateOptions.builder()
                        .aliases(Collections.singletonList(domainName))
                        .build()))
                .build();
        ARecord.Builder.create(this, project.getName() + "-a-record-" + "-" + subdomain  + "-cloudfront-distribution")
                .zone(zone)
                .recordName(domainName)
                .target(RecordTarget.fromAlias(new CloudFrontTarget(cloudFrontWebDistribution)))
                .build();
        return cloudFrontWebDistribution;
    }

    private void addEnvironmentVariablesToFunction(Map<String, String> environmentVariables,
                                                   Function function) {
        for (String k : environmentVariables.keySet()) {
            String value =  environmentVariables.get(k);
            if (StringUtils.isNotEmpty(value)) {
                function.addEnvironment(k, value);
            }
        }
    }

    private String functionHandler(Module module) {
        return module.getPackageName() + ".FunctionRequestHandler";
    }

    private IHostedZone findZone() {
        return HostedZone.fromLookup(this, project.getName() + "-zone",
                HostedZoneProviderProps.builder()
                        .domainName(project.getDomainName())
                        .build());
    }

    private Certificate createCertificate(IHostedZone zone) {
        return Certificate.Builder.create(this, project.getName() + "-certificate")
                .domainName(project.getDomainName())
                .subjectAlternativeNames(List.of("*." + project.getDomainName()))
                .validation(CertificateValidation.fromDns(zone))
                .build();
    }

    private void output(LambdaRestApi api) {
        CfnOutput.Builder.create(this, "ApiUrl")
                .exportName("ApiUrl")
                .value(api.getUrl())
                .build();

        CfnOutput.Builder.create(this, "DomainUrl")
                .exportName("DomainUrl")
                .value(HTTPS + project.getDomainName())
                .build();
    }

    private LambdaRestApi createApi(String subdomain, Function function, Certificate cert, IHostedZone zone) {
        LambdaRestApi api = LambdaRestApi.Builder.create(this, project.getName() + "-lambda-rest-api")
                .handler(function)
                .domainName(DomainNameOptions.builder()
                        .domainName(subdomain + "." + project.getDomainName())
                        .certificate(cert)
                        .build())
                .build();

        ARecord.Builder.create(this, project.getName() + "-a-record-lambda-rest-api")
                .zone(zone)
                .target(RecordTarget.fromAlias(new ApiGateway(api)))
                .build();
        return api;
    }

    private Map<String, String> createAuthorizationServer(Certificate cert,
                                                          IHostedZone zone,
                                                          Function postConfirmationFunction) {
        UserPool userPool = createUserPool(cert, zone, postConfirmationFunction);
        UserPoolClient userPoolClient = createUserPoolClient(userPool);
        return Map.of("COGNITO_POOL_ID", userPool.getUserPoolId(),
                "COGNITO_REGION", "us-east-1",
                    "OAUTH_CLIENT_ID", userPoolClient.getUserPoolClientId(),
                    "OAUTH_CLIENT_SECRET", "");
    }

    private Function.Builder createFunction(Map<String, String> environmentVariables,
                                   String moduleName,
                                   String handler) {
        return createFunction(environmentVariables, ApplicationType.FUNCTION, moduleName, handler, false);
    }

    private Function.Builder createFunctionGraalvm(Map<String, String> environmentVariables,
                                            String moduleName,
                                            String handler) {
        return createFunction(environmentVariables, ApplicationType.FUNCTION, moduleName, handler, true);
    }

    private Function.Builder createAppFunction(Map<String, String> environmentVariables,
                                               String moduleName,
                                               boolean graalvm) {
        return createFunction(environmentVariables, ApplicationType.DEFAULT, moduleName, null, graalvm);
    }
    private Function.Builder createAppFunction(Map<String, String> environmentVariables,
                                       String moduleName) {
        return createFunction(environmentVariables, ApplicationType.DEFAULT, moduleName, null, false);

    }

    private Function.Builder createFunction(Map<String, String> environmentVariables,
                                   ApplicationType applicationType,
                                   String moduleName,
                                   String handler,
                                            boolean graalvm) {
        Function.Builder builder =  MicronautFunction.create(applicationType,
                        graalvm,
                this,
                project.getName() + moduleName + "-java-function")
                .environment(environmentVariables)
                .code(Code.fromAsset(functionPath(moduleName, graalvm)))
                .timeout(Duration.seconds(20))
                .memorySize(1024)
                .tracing(Tracing.ACTIVE)
                .logRetention(RetentionDays.FIVE_DAYS);

        return (handler != null) ? builder.handler(handler) : builder;
    }

    public static String functionPath(String moduleName, boolean graalvm) {
        return "../" + moduleName + "/build/libs/" + functionFilename(moduleName, graalvm);
    }

    public static String functionFilename(String moduleName, boolean graalvm) {
        return MicronautFunctionFile.builder()
            .graalVMNative(graalvm)
            .version("0.1")
            .archiveBaseName(moduleName)
            .buildTool(BuildTool.GRADLE)
            .build();
    }

    public Table createTable() {
        Table table = Table.Builder.create(this, project.getName() + "-table")
                .partitionKey(Attribute.builder()
                        .name(ATTRIBUTE_PK)
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name(ATTRIBUTE_SK)
                        .type(AttributeType.STRING)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();
        table.addGlobalSecondaryIndex(globalSecondaryIndexProps(INDEX_GSI_1,
                ATTRIBUTE_GSI_1_PK,
                ATTRIBUTE_GSI_1_SK));
        return table;
    }

    private GlobalSecondaryIndexProps globalSecondaryIndexProps(String indexName,
                                                                String pk,
                                                                String sk) {
        return GlobalSecondaryIndexProps.builder()
                .indexName(indexName)
                .partitionKey(Attribute.builder()
                        .name(pk)
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name(sk)
                        .type(AttributeType.STRING)
                        .build())
                .build();
    }

    public UserPool createUserPool(Certificate cert,
                                   IHostedZone zone,
                                   Function postConfirmationFunction) {
        UserPool userPool = UserPool.Builder.create(this, project.getName() + "-userpool")
                .signInAliases(SignInAliases.builder()
                        .phone(false)
                        .username(false)
                        .email(true)
                        .build())
                .selfSignUpEnabled(true)
                .build();
        userPool.addTrigger(UserPoolOperation.POST_CONFIRMATION, postConfirmationFunction);

        //Uncommented after you have run cdk deploy once and an A record exists for the zone
        addDomain(cert, zone, userPool);
        return userPool;
    }

    private void verifiedSes() {
        CfnEmailIdentity.Builder.create(this, project.getName() + "-email-identity")
                .dkimAttributes(CfnEmailIdentity.DkimAttributesProperty.builder()
                        .signingEnabled(Boolean.TRUE)
                        .build())
                .mailFromAttributes(CfnEmailIdentity.MailFromAttributesProperty.builder()
                        .mailFromDomain(project.getDomainName())
                        .build())
                .build();
    }

    private void addDomain(Certificate cert, IHostedZone zone, UserPool userPool) {
        String domainName = "auth." + project.getDomainName();
        UserPoolDomain userPoolDomain = userPool.addDomain(project.getName() + "-userpool-domain", UserPoolDomainOptions.builder()
                .customDomain(createCustomDomainOptions(cert, domainName))
                .build());
        //ARecord.Builder.create(this, project.getName() + "-a-record-cognito-userpool")
        //    .zone(zone)
        //    .recordName(domainName)
        //    .target(RecordTarget.fromAlias(new UserPoolDomainTarget(userPoolDomain)))
        //    .build();
    }

    public  CustomDomainOptions createCustomDomainOptions(Certificate cert, String domainName) {
        return CustomDomainOptions.builder()
                .domainName(domainName)
                .certificate(cert)
                .build();
    }

    private UserPoolClient createUserPoolClient(UserPool userPool) {
        UserPoolClientOptions clientOptions = UserPoolClientOptions.builder()
                .generateSecret(true)
                .oAuth(OAuthSettings.builder()
                        .scopes(Arrays.asList(OAuthScope.PROFILE,
                                OAuthScope.EMAIL,
                                OAuthScope.OPENID))
                        .flows(OAuthFlows.builder()
                                .authorizationCodeGrant(true)
                                .build())
                        .callbackUrls(Stream.of(HTTPS + project.getDomainName(), LOCALHOST)
                                .map(domain -> domain + "/oauth/callback/" + OAUTH_CLIENT_NAME)
                                .collect(Collectors.toList()))
                        .logoutUrls(Stream.of(HTTPS + project.getDomainName(), LOCALHOST)
                                .map(domain -> domain + "/logout")
                                .collect(Collectors.toList()))
                        .build())
                .build();
        return userPool.addClient(project.getName() + "-userpool-client", clientOptions);
    }
}