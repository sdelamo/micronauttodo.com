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
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.DomainMappingOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.DomainNameAttributes;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketRouteOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketStage;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.WebSocketLambdaIntegration;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.CertificateValidation;
import software.amazon.awscdk.services.cloudfront.Behavior;
import software.amazon.awscdk.services.cloudfront.CloudFrontAllowedMethods;
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
import software.amazon.awscdk.services.lambda.Alias;
import software.amazon.awscdk.services.lambda.AliasAttributes;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.lambda.Version;
import software.amazon.awscdk.services.lambda.VersionAttributes;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.route53.ARecord;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProviderProps;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.amazon.awscdk.services.route53.RecordTarget;
import software.amazon.awscdk.services.route53.targets.ApiGateway;
import software.amazon.awscdk.services.route53.targets.ApiGatewayv2DomainProperties;
import software.amazon.awscdk.services.route53.targets.CloudFrontTarget;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.CorsRule;
import software.amazon.awscdk.services.s3.HttpMethods;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.Source;
import software.amazon.awscdk.services.ses.CfnEmailIdentity;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.*;
import static software.amazon.awscdk.services.apigatewayv2.alpha.PayloadFormatVersion.VERSION_1_0;

public class AppStack extends Stack {

    private static final String OAUTH_CLIENT_NAME = "cognito";

    private static final String LOCALHOST = "http://localhost:8080";

    private static final String HTTPS = "https://";
    public static final int MEMORY_SIZE = 2024;
    public static final int TIMEOUT = 20;

    private final Project project;

    public AppStack(final Project project, final Construct parent, final String id) {
        this(project, parent, id, null);
    }

    public AppStack(final Project project, final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);
        this.project = project;

        Table table = createTable();

        Module appModule = project.findModuleByName(Main.MODULE_APP);

        Function function = createAppFunction(environmentVariables(table), appModule).build();
        table.grantReadWriteData(function);
        LambdaRestApi api = createRestApi(appModule, function);

        Module appModuleExtra = project.findModuleByName(Main.MODULE_APP_EXTRA);

        Function functionExtra = createAppFunction(environmentVariables(table), appModuleExtra).build();
        table.grantReadWriteData(functionExtra);

        LambdaRestApi apiExtra = createRestApi(appModuleExtra, functionExtra);
        //TODO comment the above line and uncomment the following
        /*
        String versionNumber = "4"; // Change Me
        Alias alias = Alias.Builder.create(this, "LambdaAlias")
                .version(Version.fromVersionAttributes(this, "Version", VersionAttributes.builder()
                                .version(versionNumber)
                                .lambda(functionExtra)
                        .build()))
                .aliasName("prod")
                .build();
        LambdaRestApi apiExtra = createRestApi(appModuleExtra, alias);
        */


        Module appModuleNative = project.findModuleByName(Main.MODULE_APP_GRAALVM);
        Function functionNative = createAppFunction(environmentVariables(table), appModuleNative, true).build();
        table.grantReadWriteData(functionNative);

        LambdaRestApi apiNative = createRestApi(appModuleNative, functionNative);

        output(api, "JavaApi");
        output(apiExtra, "JavaExtraApi");
        output(apiNative, "GraalVMNativeApi");
    }


    private static Map<String, String> environmentVariables(Table table) {
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("DYNAMODB_TABLE_NAME", table.getTableName());
        // https://aws.amazon.com/blogs/compute/optimizing-aws-lambda-function-performance-for-java/
        environmentVariables.put("JAVA_TOOL_OPTIONS", "-XX:+TieredCompilation -XX:TieredStopAtLevel=1");
        return  environmentVariables;
    }

    private void addCorsRule(Bucket bucket, String allowedOrigin) {
        bucket.addCorsRule(CorsRule.builder()
                .allowedMethods(Arrays.asList(HttpMethods.GET, HttpMethods.POST, HttpMethods.PUT, HttpMethods.DELETE, HttpMethods.HEAD))
                .allowedHeaders(Collections.singletonList("*"))
                .allowedOrigins(Collections.singletonList(allowedOrigin))
                .build());
    }

    private void createBucketDeployment(Bucket bucket, String moduleName) {
        BucketDeployment.Builder.create(this, project.getName() + "-s3-deployment-" + moduleName)
                .sources(Collections.singletonList(Source.asset("../"+ moduleName)))
                .destinationBucket(bucket)
                .build();
    }

    software.amazon.awscdk.services.apigatewayv2.alpha.DomainName createWebSocketApiDomain(String domain,
                                                                                           String id,
                                                                                           Certificate cert,
                                                                                           IHostedZone zone) {
        software.amazon.awscdk.services.apigatewayv2.alpha.DomainName domainName =
                software.amazon.awscdk.services.apigatewayv2.alpha.DomainName.Builder.create(this, id)
                .certificate(cert)
                .domainName(domain)
                .build();
        ApiGatewayv2DomainProperties domainProperties =
                new ApiGatewayv2DomainProperties(domainName.getRegionalDomainName(), domainName.getRegionalHostedZoneId());
        ARecord.Builder.create(this, project.getName() + "-a-record-websockets-api")
                .zone(zone)
                .recordName(domain)
                .target(RecordTarget.fromAlias(domainProperties))
                .build();
        return domainName;
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

    private WebSocketStage createWebSocketStage(String projectName,
                                                WebSocketApi webSocketApi,
                                                software.amazon.awscdk.services.apigatewayv2.alpha.DomainName domainName) {
        return WebSocketStage.Builder.create(this, projectName + "-function-api-websocket-stage-production")
                .webSocketApi(webSocketApi)
                .stageName("production")
                .autoDeploy(true)
                .domainMapping(DomainMappingOptions.builder()
                        .domainName(domainName)
                        .build())
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
                                .originAccessIdentity(OriginAccessIdentity.Builder.create(this, project.getName() + "-" + subdomain + "-origin-access-identity")
                                        .build())
                                .build())
                        .behaviors(Collections.singletonList(Behavior.builder()
                                .isDefaultBehavior(true)
                                .viewerProtocolPolicy(ViewerProtocolPolicy.REDIRECT_TO_HTTPS)
                                .allowedMethods(CloudFrontAllowedMethods.GET_HEAD_OPTIONS)
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
        environmentVariables.keySet().forEach(k -> {
            String value = environmentVariables.get(k);
            if (StringUtils.isNotEmpty(value)) {
                function.addEnvironment(k, value);
            }
        });
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

    private void output(LambdaRestApi api, String exportName) {
        CfnOutput.Builder.create(this, exportName)
                .exportName(exportName)
                .value(api.getUrl())
                .build();
    }

    private HttpApi createHttpApi(String subdomain, Function function, Certificate cert, IHostedZone zone) {
        HttpLambdaIntegration integration = HttpLambdaIntegration.Builder.create("HttpLambdaIntegration", function)
                .payloadFormatVersion(VERSION_1_0)
                .build();
        String domainName = subdomain != null ?
                subdomain + "." + project.getDomainName() : project.getDomainName();
        software.amazon.awscdk.services.apigatewayv2.alpha.IDomainName iDomainName =  software.amazon.awscdk.services.apigatewayv2.alpha.DomainName.fromDomainNameAttributes(this, subdomain + "http-api-domain", DomainNameAttributes.builder()
                        .name(domainName)
                .build());
        HttpApi api = HttpApi.Builder.create(this, "micronaut-function-api")
                .defaultIntegration(integration)
                .defaultDomainMapping(DomainMappingOptions.builder()
                        .domainName(iDomainName)
                        .build())
                .build();
        /*
        ARecord.Builder.create(this, project.getName() + "-a-record-lambda-http-api")
                .zone(zone)
                .recordName(domainName)
                .target(RecordTarget.fromAlias())
                .build();
        */
        return api;
    }
    private LambdaRestApi createRestApi(Module module, Function function) {
        return createRestApiBuilder(module)
                .handler(function)
                .build();
    }

    private LambdaRestApi.Builder createRestApiBuilder(Module module) {
        return LambdaRestApi.Builder.create(this, project.getName() + "-lambda-rest-api" + module.getName());
    }

    private LambdaRestApi createRestApi(Module module, Alias alias) {
        return createRestApiBuilder(module)
                .handler(alias)
                .build();
    }


    private Map<String, String> createAuthorizationServer(Certificate cert,
                                                          IHostedZone zone,
                                                          Function postConfirmationFunction,
                                                          String webappDomainName) {
        UserPool userPool = createUserPool(cert, zone, postConfirmationFunction);
        UserPoolClient userPoolClient = createUserPoolClient(webappDomainName, userPool);

        return Map.of("COGNITO_POOL_ID", userPool.getUserPoolId(),
                "COGNITO_REGION", "us-east-1",
                    "OAUTH_CLIENT_ID", userPoolClient.getUserPoolClientId(),
                "OAUTH_CLIENT_SECRET", userPoolClient.getUserPoolClientSecret().unsafeUnwrap());
    }

    private Function.Builder createFunction(Map<String, String> environmentVariables,
                                   Module module,
                                   String handler) {
        return createFunction(environmentVariables, ApplicationType.FUNCTION, module, handler, false);
    }

    private Function.Builder createFunctionGraalvm(Map<String, String> environmentVariables,
                                            Module module,
                                            String handler) {
        return createFunction(environmentVariables, ApplicationType.FUNCTION, module, handler, true);
    }

    private Function.Builder createAppFunction(Map<String, String> environmentVariables,
                                               Module module,
                                               boolean graalvm) {
        return createFunction(environmentVariables, ApplicationType.DEFAULT, module, null, graalvm);
    }
    private Function.Builder createAppFunction(Map<String, String> environmentVariables,
                                       Module module) {
        return createFunction(environmentVariables, ApplicationType.DEFAULT, module, null, false);

    }

    private Function.Builder createFunction(Map<String, String> environmentVariables,
                                   ApplicationType applicationType,
                                   Module module,
                                   String handler,
                                            boolean graalvm) {
        Function.Builder builder =  MicronautFunction.create(applicationType,
                        graalvm,
                this,
                project.getName() + module.getName() + "-java-function")
                .environment(environmentVariables)
                .code(Code.fromAsset(functionPath(module.getPath(), graalvm)))
                .timeout(Duration.seconds(TIMEOUT))
                .memorySize(MEMORY_SIZE)
                .tracing(Tracing.DISABLED)
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
        table.addGlobalSecondaryIndex(globalSecondaryIndexProps(INDEX_GSI_2,
                ATTRIBUTE_GSI_2_PK,
                ATTRIBUTE_GSI_2_SK));
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

    private UserPoolClient createUserPoolClient(String webappDomainName, UserPool userPool) {
        UserPoolClientOptions clientOptions = UserPoolClientOptions.builder()
                .generateSecret(true)
                .oAuth(OAuthSettings.builder()
                        .scopes(Arrays.asList(OAuthScope.PROFILE,
                                OAuthScope.EMAIL,
                                OAuthScope.OPENID))
                        .flows(OAuthFlows.builder()
                                .authorizationCodeGrant(true)
                                .build())
                        .callbackUrls(Stream.of(webappDomainName, LOCALHOST)
                                .map(domain -> domain + "/oauth/callback/" + OAUTH_CLIENT_NAME)
                                .collect(Collectors.toList()))
                        .logoutUrls(Stream.of(webappDomainName, LOCALHOST)
                                .map(domain -> domain + "/logout")
                                .collect(Collectors.toList()))
                        .build())
                .build();
        return userPool.addClient(project.getName() + "-userpool-client", clientOptions);
    }
}