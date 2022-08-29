package com.micronauttodo.repositories;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import io.micronaut.core.annotation.NonNull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface WebSocketConnectionRepository {

    void save(@NonNull @NotNull OAuthUser user,
              @NonNull @NotNull @Valid WebSocketConnection connection);

    void delete(@NonNull @NotNull @Valid WebSocketConnection connection);

    @NonNull
    List<WebSocketConnection> findAllByUser(@NonNull @NotNull OAuthUser user);
}
