package com.micronauttodo.views;

import com.micronauttodo.conf.AssetsConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.model.ViewModelProcessor;
import jakarta.inject.Singleton;

@Requires(beans = AssetsConfiguration.class)
@Singleton
public class AssetsModelViewModelProcessor implements ViewModelProcessor<Model> {

    private final AssetsConfiguration assetsConfiguration;

    public AssetsModelViewModelProcessor(AssetsConfiguration assetsConfiguration) {
        this.assetsConfiguration = assetsConfiguration;
    }

    @Override
    public void process(@NonNull HttpRequest<?> request, @NonNull ModelAndView<Model> modelAndView) {
        modelAndView.getModel().ifPresent(m -> m.setAssetsUrl(assetsConfiguration.getUrl()));
    }
}
