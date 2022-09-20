package com.micronauttodo.repositories.microstream;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import io.micronaut.core.annotation.Introspected;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Introspected
public class Users {
    private Map<OAuthUser, Set<WebSocketConnection>> connections = new HashMap<>();

    public Map<OAuthUser, Set<WebSocketConnection>> getConnections() {
        return connections;
    }
}
