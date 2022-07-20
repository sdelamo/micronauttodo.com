package com.micronauttodo.models;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotBlank;

@Introspected
public class WebSocketConnection {

    @NonNull
    @NotBlank
    private final String region;

    @NonNull
    @NotBlank
    private final String apiId;

    @NonNull
    @NotBlank
    private final String stage;

    @NonNull
    @NotBlank
    private final String connectionId;

    @Nullable
    private final String domainName;

    public WebSocketConnection(@NonNull String region,
                               @NonNull String apiId,
                               @NonNull String stage,
                               @NonNull String connectionId,
                               @Nullable String domainName) {
        this.region = region;
        this.apiId = apiId;
        this.stage = stage;
        this.connectionId = connectionId;
        this.domainName = domainName;
    }

    @NonNull
    public String getRegion() {
        return region;
    }

    @NonNull
    public String getApiId() {
        return apiId;
    }

    @NonNull
    public String getStage() {
        return stage;
    }

    @NonNull
    public String getConnectionId() {
        return connectionId;
    }

    @Nullable
    public String getDomainName() {
        return domainName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebSocketConnection that = (WebSocketConnection) o;

        if (!apiId.equals(that.apiId)) return false;
        if (!stage.equals(that.stage)) return false;
        if (!connectionId.equals(that.connectionId)) return false;
        return domainName != null ? domainName.equals(that.domainName) : that.domainName == null;
    }

    @Override
    public int hashCode() {
        int result = apiId.hashCode();
        result = 31 * result + stage.hashCode();
        result = 31 * result + connectionId.hashCode();
        result = 31 * result + (domainName != null ? domainName.hashCode() : 0);
        return result;
    }
}
