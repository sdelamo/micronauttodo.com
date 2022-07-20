package com.micronauttodo.conf;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;

@Requires(property = "assets.url")
@ConfigurationProperties("assets")
public interface AssetsConfiguration {
    @NonNull
    String getUrl();
}
