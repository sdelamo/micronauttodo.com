package com.micronauttodo.models.events;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.models.WebSocketMessage;
import com.micronauttodo.repositories.WebSocketConnectionRepository;
import com.micronauttodo.utils.WritableUtils;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.TurboStreamRenderer;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Requires(beans = WebSocketMessagePublisher.class)
@Singleton
public class WebsocketsTurboStreamPublisherImpl implements WebsocketsTurboStreamPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(WebsocketsTurboStreamPublisherImpl.class);

    private final WebSocketMessagePublisher webSocketMessagePublisher;
    private final TurboStreamRenderer turboStreamRenderer;
    private final WebSocketConnectionRepository webSocketConnectionRepository;


    public WebsocketsTurboStreamPublisherImpl(WebSocketMessagePublisher webSocketMessagePublisher,
                                    TurboStreamRenderer turboStreamRenderer,
                                    WebSocketConnectionRepository webSocketConnectionRepository) {
        this.webSocketMessagePublisher = webSocketMessagePublisher;
        this.turboStreamRenderer = turboStreamRenderer;
        this.webSocketConnectionRepository = webSocketConnectionRepository;
    }

    @Override
    public void publish(@NonNull @NotNull @Valid OAuthUser user,
                        @NonNull @NotNull TurboStream.Builder turboStream) {
        List<WebSocketConnection> connections = webSocketConnectionRepository.findAllByUser(user);
        if (LOG.isTraceEnabled()) {
            LOG.trace("#connections {}", connections.size());
        }
        if (CollectionUtils.isNotEmpty(connections)) {
            turboStreamRenderer.render(turboStream, null)
                    .flatMap(WritableUtils::writableToString)
                    .ifPresent(message -> {
                        connections.stream()
                                .map(connection -> new WebSocketMessage(connection, message))
                                .forEach(webSocketMessagePublisher::publish);
                    });
        }
    }
}
