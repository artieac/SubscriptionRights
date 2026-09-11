package com.alwaysmoveforward.subscriptionrights.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the "bearer" security scheme used by the /api/external/** endpoints, which
 * authenticate via a plain "Authorization: Bearer <token>" header -- see ApiTokenAuthenticationFilter.
 * This only makes the scheme available for Swagger UI's "Authorize" button; it is not applied
 * globally, since the cookie-authenticated /api/** admin endpoints don't use it.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI subscriptionRightsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SubscriptionRights API")
                        .description("Subscription plans, plan sets, entitlements, and grants.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes("apiToken", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("Opaque")
                                .description("API token for /api/external/** endpoints.")));
    }
}
