package com.micronauttodo.websocket.server;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.events.WebsocketsTurboStreamPublisher;
import com.micronauttodo.utils.OauthUserUtils;
import com.micronauttodo.utils.WritableUtils;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.io.Writable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;
import io.micronaut.security.token.validator.TokenValidator;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.TurboStreamRenderer;
import io.micronaut.views.turbo.http.TurboMediaType;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Secured(SecurityRule.IS_ANONYMOUS)
@ServerWebSocket("/todo{?token}")
class TodoWebSocketServer implements WebsocketsTurboStreamPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(TodoWebSocketServer.class);
    private final TurboStreamRenderer turboStreamRenderer;
    private final WebSocketBroadcaster broadcaster;
    private final TokenValidator tokenValidator;
    private Map<OAuthUser, Set<String>> userSessions = new ConcurrentHashMap<>();

    TodoWebSocketServer(TurboStreamRenderer turboStreamRenderer,
                        WebSocketBroadcaster broadcaster,
                        TokenValidator tokenValidator) {
        this.turboStreamRenderer = turboStreamRenderer;
        this.broadcaster = broadcaster;
        this.tokenValidator = tokenValidator;
    }

    @OnOpen
    public void onOpen(String token, WebSocketSession session) {
        LOG.info("onOpen {}", token);
        Mono.from(tokenValidator.validateToken(token, null)).subscribe(authentication -> {
                    OAuthUser user = OauthUserUtils.toOauthUser(authentication);
                    LOG.info("token validated for user {}", user.getSub());
                    userSessions.computeIfPresent(user, (k, sessions) -> {
                        sessions.add(session.getId());
                        return sessions;
                    });
                    userSessions.computeIfAbsent(user, k -> {
                        Set<String> result = new HashSet<>();
                        result.add(session.getId());
                        return result;
                    });
                });
    }

    @OnMessage
    public void onMessage(String message, WebSocketSession session) {
        LOG.info("onMessage {}", message);
    }

    @OnClose
    public void onClose(String token, WebSocketSession session) {
        LOG.info("onClose token {}", token);
        Mono.from(tokenValidator.validateToken(token, null)).subscribe(authentication -> {
            OAuthUser user = OauthUserUtils.toOauthUser(authentication);
            LOG.info("token validated for user {}", user.getSub());
            userSessions.computeIfPresent(user, (k, sessions) -> {
                sessions.remove(session.getId());
                return sessions;
            });
        });
    }

    @Override
    public void publish(@NonNull @NotNull @Valid OAuthUser user,
                        @NonNull @NotNull TurboStream.Builder turboStream) {
        turboStreamRenderer.render(turboStream, null)
                .ifPresent(writable -> broadcast(writable, user));
    }

    private void broadcast(@NonNull Writable writable, @NonNull OAuthUser user) {
        WritableUtils.writableToString(writable)
                .ifPresent(message -> broadcaster.broadcastAsync(message, TurboMediaType.TURBO_STREAM_TYPE, inUser(user)));
    }

    @NonNull
    private Predicate<WebSocketSession> inUser(@NonNull OAuthUser user) {
        Set<String> websocketIds = userSessions.getOrDefault(user, Collections.emptySet());
        return s -> websocketIds.contains(s.getId());
    }
}
