package com.micronauttodo.repositories.microstream;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.repositories.WebSocketConnectionRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.microstream.RootProvider;
import io.micronaut.microstream.annotations.StoreParams;
import io.micronaut.microstream.annotations.StoreReturn;
import jakarta.inject.Singleton;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class WebSocketConnectionRepositoryImpl implements WebSocketConnectionRepository {

    private final RootProvider<RootData> rootProvider;

    public WebSocketConnectionRepositoryImpl(RootProvider<RootData> rootProvider) {
        this.rootProvider = rootProvider;
    }

    @Override
    public void save(@NonNull @NotNull OAuthUser user,
                     @NonNull @NotNull @Valid WebSocketConnection connection) {
        if (rootProvider.root().getConnections().containsKey(user)) {
            append(rootProvider.root().getConnections(), user, connection);
        } else {
            add(rootProvider.root().getConnections(), user, connection);
        }
    }

    @Override
    public void delete(@NonNull @NotNull @Valid WebSocketConnection connection) {
        Map<OAuthUser, Set<WebSocketConnection>> m = rootProvider.root().getConnections();
        OAuthUser belongsTo = null;
        for (OAuthUser user : m.keySet()) {
            if (m.get(user).contains(connection)) {
                belongsTo = user;
                break;
            }
        }
        if (belongsTo != null) {
            delete(rootProvider.root().getConnections(), belongsTo, connection);
        }
    }

    @Override
    @NonNull
    public List<WebSocketConnection> findAllByUser(@NonNull @NotNull OAuthUser user) {
        Map<OAuthUser, Set<WebSocketConnection>> m = rootProvider.root().getConnections();
        return m.containsKey(user) ?
                new ArrayList<>(m.get(user)) :
                Collections.emptyList();
    }

    @StoreParams("connections")
    void add(@NonNull Map<OAuthUser, Set<WebSocketConnection>> connections,
             @NonNull OAuthUser user,
             @NonNull WebSocketConnection connection) {
        Set<WebSocketConnection> value = new HashSet<>();
        value.add(connection);
        connections.put(user, value);
    }

    @StoreReturn
    Set<WebSocketConnection> append(@NonNull Map<OAuthUser, Set<WebSocketConnection>> connections,
                                     @NonNull OAuthUser user,
                                     @NonNull WebSocketConnection connection) {
        connections.get(user).add(connection);
        return connections.get(user);
    }

    @StoreReturn
    Set<WebSocketConnection> delete(@NonNull Map<OAuthUser, Set<WebSocketConnection>> connections,
                                     @NonNull OAuthUser user,
                                     @NonNull WebSocketConnection connection) {
        Set<WebSocketConnection> userConnections = connections.get(user);
        userConnections.remove(connection);
        connections.put(user, userConnections);
        return connections.get(user);
    }

    public int countByUser(@NonNull OAuthUser oAuthUser) {
        return findAllByUser(oAuthUser).size();
    }
}
