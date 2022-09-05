package com.micronauttodo.models.events;

import com.micronauttodo.utils.TurboUtils;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.TurboStreamAction;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Requires(bean = WebsocketsTurboStreamPublisher.class)
@Singleton
public class TodoDeletedEventListener implements ApplicationEventListener<TodoDeletedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(TodoDeletedEventListener.class);

    private final WebsocketsTurboStreamPublisher websocketsTurboStreamPublisher;

    public TodoDeletedEventListener(WebsocketsTurboStreamPublisher websocketsTurboStreamPublisher) {
        this.websocketsTurboStreamPublisher = websocketsTurboStreamPublisher;
    }

    @Override
    public void onApplicationEvent(TodoDeletedEvent event) {
        websocketsTurboStreamPublisher.publish(event.getUser(), TurboUtils.remove(event.getId()));
    }
}
