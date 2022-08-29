package com.micronauttodo.websockets.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.repositories.WebSocketConnectionRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectTest extends AbstractTest {

    private static final String JWT = "eyJraWQiOiIzY1wvWHFLVHZmYTVqSWJubWgwVXlWM3NjeUdrXC96SWJvblIrK1R1NElhTWM9IiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoiZnBKdlRYQ0Z1Q1ozSDRJZG10V2FKQSIsInN1YiI6ImMyMTIzYjk2LWViMTQtNGYyNi05NzM1LWU5ZjBlNzJhOGY5ZCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9nMUoybXU2bzAiLCJjb2duaXRvOnVzZXJuYW1lIjoiYzIxMjNiOTYtZWIxNC00ZjI2LTk3MzUtZTlmMGU3MmE4ZjlkIiwibm9uY2UiOiI4MDNjMjI2NS04ZjIzLTRjOTAtYTFmMy02MzAwODA2ODExYzEiLCJvcmlnaW5fanRpIjoiYTc0MjViNTctNjYxMC00NDY3LTk3MTMtYTk4OTA0YzlkMTg4IiwiYXVkIjoiNjhnMTE4cjBtNms0YTZocDUxOTJrcW1vNmQiLCJldmVudF9pZCI6ImQ1ZTAzZjU1LWQxODQtNGZmMS04NjU4LTdjZjk2OTZiZWMyNSIsInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNjYxNzY3MjQwLCJleHAiOjE2NjE3NzA4NDAsImlhdCI6MTY2MTc2NzI0MCwianRpIjoiYTM2ZGUyMmEtZmU3My00MTJkLTg3YjQtOTM4YWI2M2JkMWJlIiwiZW1haWwiOiJzZXJnaW8uZGVsYW1vQHNvZnRhbW8uY29tIn0.Y4GN53PRazqrUA3jqJEOaWQ3_a_ERR4whLtwKC5rH-7FIjz3dumtChOD-pU1RH6ZjU2RkEvUIPCB4RllirWvVvbMn21Ta84YiXOa2Fmm6fis2x92-os8iSiRsHZ6px8UVZMnF0CGHNrstAklvZ_4rwGwOFX8O6iZMA6B9uLsxN0SXNU3gmdHObJxZeEHH1ZdZ5M_-hmF6knqg6jTPgSBOkY2ma3CASKd9yulgQ7wUoyVC-FhdhObjiUc2RlmNig0CEea58IUk7uTOtrHgv9UsWTKDOjoIFWad4sU_rlVH02WkFevXEqCa93fJ-qEVbB90fB4VMI_Hw21-6N7JmtASQ";
    private static final String API_ID = "3opvlw0fpd";
    public static final String PRODUCTION = "production";
    public static final String CONNECTION_ID = "Xn48TcG2IAMCFig=";
    public static final String DOMAIN_NAME = "websockets.micronauttodo.com";

    @Test
    void connectCreatesAnEntryInTheDabase() throws IOException {
        Optional<OAuthUser> oAuthUserOptional = OAuthUserUtils.userOfToken(JWT);
        assertTrue(oAuthUserOptional.isPresent());
        OAuthUser oAuthUser = oAuthUserOptional.get();

        FunctionRequestHandler handler = getHandler();
        handler.execute(connect());

        WebSocketConnectionRepository webSocketConnectionRepository = handler.getApplicationContext().getBean(WebSocketConnectionRepository.class);

        List<WebSocketConnection> connectionsByUser = webSocketConnectionRepository.findAllByUser(oAuthUser);

        assertFalse(connectionsByUser.isEmpty());

        handler.execute(disconnect());

        connectionsByUser = webSocketConnectionRepository.findAllByUser(oAuthUser);

        assertTrue(connectionsByUser.isEmpty());

        handler.close();
        getDynamoDbLocal().stop();
    }

    APIGatewayV2WebSocketEvent connect() {
        APIGatewayV2WebSocketEvent input = new APIGatewayV2WebSocketEvent();
        input.setResource(null);
        input.setPath(null);
        input.setHttpMethod(null);

        Map<String, String> headers = new HashMap<>();
        headers.put("Host", DOMAIN_NAME);
        headers.put("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits");
        headers.put("Sec-WebSocket-Key", "KPU5cQrqeyqiKJIyrOJ9xA==");
        headers.put("Sec-WebSocket-Version", "13");
        headers.put("X-Amzn-Trace-Id=Root","1-630c8c17-71c352e254a965055eee2f63");
        headers.put("X-Forwarded-For","80.26.205.48");
        headers.put("X-Forwarded-Port", "443");
        headers.put("X-Forwarded-Proto", "https");
        input.setHeaders(headers);
        Map<String, List<String>> multiHeaders = new HashMap<>();
        for (String k : headers.keySet()) {
            multiHeaders.put(k, Collections.singletonList(headers.get(k)));
        }
        input.setMultiValueHeaders(multiHeaders);
        Map<String, String> queryStringParameters = new HashMap<>();
        queryStringParameters.put("token", JWT);
        input.setQueryStringParameters(queryStringParameters);
        Map<String, List<String>> multiValueQueryStringParameters = new HashMap<>();
        for (String k : queryStringParameters.keySet()) {
            multiValueQueryStringParameters.put(k, Collections.singletonList(queryStringParameters.get(k)));
        }
        input.setMultiValueQueryStringParameters(multiValueQueryStringParameters);
        input.setPathParameters(null);
        input.setStageVariables(null);
        APIGatewayV2WebSocketEvent.RequestContext requestContext = new APIGatewayV2WebSocketEvent.RequestContext();
        requestContext.setAccountId(null);
        requestContext.setResourceId(null);
        requestContext.setStage(PRODUCTION);
        requestContext.setRequestId("XnrTsHi4oAMF6ag=");
        requestContext.setIdentity(createIdentity("80.26.205.48", null));
        requestContext.setResourcePath(null);
        requestContext.setAuthorizer(null);
        requestContext.setHttpMethod(null);
        requestContext.setApiId(API_ID);
        requestContext.setConnectedAt(1661766679454L);
        requestContext.setConnectionId(CONNECTION_ID);
        requestContext.setDomainName(DOMAIN_NAME);
        requestContext.setError(null);
        requestContext.setEventType("CONNECT");
        requestContext.setExtendedRequestId("XnrTsHi4oAMF6ag=");
        requestContext.setIntegrationLatency(null);
        requestContext.setMessageDirection("IN");
        requestContext.setMessageId(null);
        requestContext.setRequestTime("29/Aug/2022:09:51:19 +0000");
        requestContext.setRequestTimeEpoch(1661766679469L);
        requestContext.setRouteKey("$connect");
        requestContext.setStatus(null);
        input.setRequestContext(requestContext);
        input.setBody(null);
        input.setIsBase64Encoded(false);
        return input;
    }

    @NonNull
    private static APIGatewayV2WebSocketEvent.RequestIdentity createIdentity(@NonNull String sourceIp, @Nullable String userAgent) {
        APIGatewayV2WebSocketEvent.RequestIdentity identity = new APIGatewayV2WebSocketEvent.RequestIdentity();
        identity.setCognitoIdentityPoolId(null);
        identity.setAccountId(null);
        identity.setCognitoIdentityId(null);
        identity.setCaller(null);
        identity.setApiKey(null);
        identity.setSourceIp(sourceIp);
        identity.setCognitoAuthenticationType(null);
        identity.setCognitoAuthenticationProvider(null);
        identity.setUserArn(null);
        identity.setUserAgent(userAgent);
        identity.setUser(null);
        identity.setAccessKey(null);
        return identity;
    }

    APIGatewayV2WebSocketEvent disconnect() {
        APIGatewayV2WebSocketEvent input = new APIGatewayV2WebSocketEvent();
        input.setResource(null);
        input.setPath(null);
        input.setHttpMethod(null);

        Map<String, String> headers = new HashMap<>();
        headers.put("Host", DOMAIN_NAME);
        headers.put("x-api-key", "");
        headers.put("X-Forwarded-For", "");
        headers.put("x-restapi", "");
        input.setHeaders(headers);
        Map<String, List<String>> multiHeaders = new HashMap<>();
        for (String k : headers.keySet()) {
            multiHeaders.put(k, Collections.singletonList(headers.get(k)));
        }
        input.setMultiValueHeaders(multiHeaders);
        input.setQueryStringParameters(null);
        input.setMultiValueQueryStringParameters(null);

        input.setPathParameters(null);
        input.setStageVariables(null);
        APIGatewayV2WebSocketEvent.RequestContext requestContext = new APIGatewayV2WebSocketEvent.RequestContext();
        requestContext.setAccountId(null);
        requestContext.setResourceId(null);
        requestContext.setStage(PRODUCTION);
        requestContext.setRequestId("Xn5CBEE2oAMFVtg=");
        requestContext.setIdentity(createIdentity("80.26.205.48", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36"));
        requestContext.setResourcePath(null);
        requestContext.setAuthorizer(null);
        requestContext.setHttpMethod(null);
        requestContext.setApiId(API_ID);
        requestContext.setConnectedAt(1661772264167L);
        requestContext.setConnectionId(CONNECTION_ID);
        requestContext.setDomainName(DOMAIN_NAME);
        requestContext.setEventType("DISCONNECT");
        requestContext.setExtendedRequestId("Xn5CBEE2oAMFVtg=");
        requestContext.setMessageDirection("IN");
        requestContext.setRequestTime("29/Aug/2022:11:25:00 +0000");
        requestContext.setRequestTimeEpoch(1661772300747L);
        requestContext.setRouteKey("$disconnect");
        requestContext.setError(null);
        input.setRequestContext(requestContext);
        input.setBody(null);
        input.setIsBase64Encoded(false);


        return input;
    }

}
