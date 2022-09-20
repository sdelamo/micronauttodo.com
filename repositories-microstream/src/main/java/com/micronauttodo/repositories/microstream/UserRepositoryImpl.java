package com.micronauttodo.repositories.microstream;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.repositories.UserRepository;
import io.micronaut.microstream.RootProvider;
import io.micronaut.microstream.annotations.StoreParams;
import jakarta.inject.Singleton;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class UserRepositoryImpl implements UserRepository {
    private final RootProvider<RootData> rootProvider;

    public UserRepositoryImpl(RootProvider<RootData> rootProvider) {
        this.rootProvider = rootProvider;
    }

    @Override
    public void save(@NotNull @Valid OAuthUser user) {
        if (!rootProvider.root().getUsers().getConnections().containsKey(user)) {
            addUser(rootProvider.root().getUsers().getConnections(), user);
        }
    }

    @Override
    public void delete(@NotNull @Valid OAuthUser user) {
        removeUser(rootProvider.root().getUsers().getConnections(), user);
    }

    @StoreParams("users")
    void addUser(Map<OAuthUser, Set<WebSocketConnection>> users, OAuthUser user) {
        users.put(user, new HashSet<>());
    }

    @StoreParams("users")
    void removeUser(Map<OAuthUser, Set<WebSocketConnection>> users, OAuthUser user) {
        users.remove(user);
    }

    public int count() {
        return rootProvider.root().getUsers().getConnections().keySet().size();
    }
}
