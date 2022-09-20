package com.micronauttodo.repositories.microstream;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import com.micronauttodo.models.WebSocketConnection;
import io.micronaut.core.annotation.Introspected;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Introspected
public class RootData {
    private Map<String, Map<String, Todo>> todos = new HashMap<>();
    private Map<OAuthUser, Set<WebSocketConnection>> connections = new HashMap<>();

    public Map<OAuthUser, Set<WebSocketConnection>> getConnections() {
        return connections;
    }
    public Map<String, Map<String, Todo>> getTodos() {
        return todos;
    }
}
