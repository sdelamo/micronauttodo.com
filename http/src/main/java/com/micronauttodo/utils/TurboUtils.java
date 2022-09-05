package com.micronauttodo.utils;

import com.micronauttodo.models.Todo;
import com.micronauttodo.models.TodoCreate;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.turbo.TurboStream;

import java.util.Collections;

public final class TurboUtils {
    TurboUtils() {
    }

    public static TurboStream.Builder append(Todo todo) {
        return TurboStream.builder()
                .template("/todo/_tr.html", Collections.singletonMap("todo", todo))
                .targetDomId("todos-rows")
                .append();
    }

    public static TurboStream.Builder remove(@NonNull String todoId) {
        return TurboStream.builder()
                .targetDomId("todo-" + todoId)
                .remove();
    }
}
