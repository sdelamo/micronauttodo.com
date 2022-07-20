package com.micronauttodo.views;

import com.micronauttodo.conf.AssetsConfiguration;
import com.micronauttodo.conf.WebsocketsConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.model.ViewModelProcessor;
import jakarta.inject.Singleton;

@Requires(beans = WebsocketsConfiguration.class)
@Singleton
public class WebsocketsModelViewModelProcessor implements ViewModelProcessor<Model> {

    private final WebsocketsConfiguration websocketsConfiguration;

    public WebsocketsModelViewModelProcessor(WebsocketsConfiguration websocketsConfiguration) {
        this.websocketsConfiguration = websocketsConfiguration;
    }

    @Override
    public void process(@NonNull HttpRequest<?> request, @NonNull ModelAndView<Model> modelAndView) {
        modelAndView.getModel().ifPresent(m -> m.setWebsocketsUrl(websocketsConfiguration.getUrl()));
    }
}
