package com.micronauttodo.views;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.model.ViewModelProcessor;
import jakarta.inject.Singleton;

@Singleton
public class AuthenticationModelViewModelProcessor implements ViewModelProcessor<Model> {
    private final SecurityService securityService;

    public AuthenticationModelViewModelProcessor(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void process(@NonNull HttpRequest<?> request, @NonNull ModelAndView<Model> modelAndView) {
        securityService.getAuthentication().ifPresent(auth ->
                modelAndView.getModel().ifPresent(m -> m.setAuthentication(auth)));
    }
}
