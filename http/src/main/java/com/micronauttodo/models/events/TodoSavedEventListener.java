package com.micronauttodo.models.events;

import com.micronauttodo.models.Todo;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.turbo.TurboStream;
import jakarta.inject.Singleton;

import java.util.Collections;

@Singleton
public class TodoSavedEventListener implements ApplicationEventListener<TodoSavedEvent> {
    private final WebsocketsTurboStreamPublisher websocketsTurboStreamPublisher;

    public TodoSavedEventListener(WebsocketsTurboStreamPublisher websocketsTurboStreamPublisher) {
        this.websocketsTurboStreamPublisher = websocketsTurboStreamPublisher;
    }

    @Override
    public void onApplicationEvent(TodoSavedEvent event) {
        websocketsTurboStreamPublisher.publish(event.getUser(), turboStream(event.getTodo()));
    }

    private TurboStream.Builder turboStream(@NonNull Todo todo) {
        return TurboStream.builder()
                .template("/todo/_tr.html", Collections.singletonMap("todo", todo))
                .targetDomId("todos-rows")
                .append();
    }
}
