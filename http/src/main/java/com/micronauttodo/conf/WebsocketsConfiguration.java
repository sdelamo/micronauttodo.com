package com.micronauttodo.conf;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;

@Requires(property = "websockets.url")
@ConfigurationProperties("websockets")
public interface WebsocketsConfiguration {
    @NonNull
    String getUrl();
}
