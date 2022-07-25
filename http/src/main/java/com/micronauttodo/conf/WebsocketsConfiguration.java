package com.micronauttodo.conf;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Requires(property = "websockets.url")
@ConfigurationProperties("websockets")
public interface WebsocketsConfiguration {
    @NotBlank
    @Pattern(regexp = "https://.*")
    @NonNull
    String getUrl();
}
