package com.micronauttodo.repositories.microstream;

import com.micronauttodo.models.Todo;
import io.micronaut.core.annotation.Introspected;

import java.util.HashMap;
import java.util.Map;

@Introspected
public class Todos {
    private Map<String, Map<String, Todo>> todos = new HashMap<>();

    public Map<String, Map<String, Todo>> getTodos() {
        return todos;
    }
}
