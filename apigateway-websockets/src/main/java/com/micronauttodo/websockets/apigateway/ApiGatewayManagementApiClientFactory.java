package com.micronauttodo.websockets.apigateway;

import io.micronaut.aws.sdk.v2.service.AwsClientFactory;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.regions.providers.AwsRegionProviderChain;

import jakarta.inject.Singleton;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiAsyncClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiAsyncClientBuilder;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClientBuilder;

/**
 * Factory that creates a Api Gateway client.
 *
 * @author Sergio del Amo
 */
@Factory
public class ApiGatewayManagementApiClientFactory extends AwsClientFactory<ApiGatewayManagementApiClientBuilder, ApiGatewayManagementApiAsyncClientBuilder, ApiGatewayManagementApiClient, ApiGatewayManagementApiAsyncClient> {

    /**
     * Constructor.
     *
     * @param credentialsProvider The credentials provider
     * @param regionProvider      The region provider
     */
    protected ApiGatewayManagementApiClientFactory(AwsCredentialsProviderChain credentialsProvider, AwsRegionProviderChain regionProvider) {
        super(credentialsProvider, regionProvider);
    }

    @Override
    protected ApiGatewayManagementApiClientBuilder createSyncBuilder() {
        return ApiGatewayManagementApiClient.builder();
    }

    @Override
    protected ApiGatewayManagementApiAsyncClientBuilder createAsyncBuilder() {
        return ApiGatewayManagementApiAsyncClient.builder();
    }

    @Override
    @Singleton
    public ApiGatewayManagementApiClientBuilder syncBuilder(SdkHttpClient httpClient) {
        return super.syncBuilder(httpClient);
    }

    @Override
    @Bean(preDestroy = "close")
    @Singleton
    public ApiGatewayManagementApiClient syncClient(ApiGatewayManagementApiClientBuilder builder) {
        return super.syncClient(builder);
    }

    @Override
    @Singleton
    @Requires(beans = SdkAsyncHttpClient.class)
    public ApiGatewayManagementApiAsyncClientBuilder asyncBuilder(SdkAsyncHttpClient httpClient) {
        return super.asyncBuilder(httpClient);
    }

    @Override
    @Bean(preDestroy = "close")
    @Singleton
    @Requires(beans = SdkAsyncHttpClient.class)
    public ApiGatewayManagementApiAsyncClient asyncClient(ApiGatewayManagementApiAsyncClientBuilder builder) {
        return super.asyncClient(builder);
    }
}
