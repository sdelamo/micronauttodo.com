package com.micronauttodo.models.events;

import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.turbo.TurboStream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface WebsocketsTurboStreamPublisher {
    void publish(@NonNull @NotNull @Valid OAuthUser user,
                 @NonNull @NotNull TurboStream.Builder turboStream);
}
