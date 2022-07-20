package com.micronauttodo.controllers.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Micronaut TODO",
                version = "1.0",
                description = "Micronaut TODO API",
                contact = @Contact(url = "https://sergiodelamo.com", name = "Sergio del Amo", email = "sergio.delamo@softamo.com")
        )
)
public final class Api {
        public static final String V1 = "v1";
        public static final String PATH = "/api/" + V1;
}
