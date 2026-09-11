package com.alwaysmoveforward.subscriptionrights.security;

import com.alwaysmoveforward.subscriptionrights.security.apitoken.ApiTokenAuthenticationFilter;
import com.alwaysmoveforward.subscriptionrights.security.jwt.JwtCookieAuthenticationFilter;
import com.alwaysmoveforward.subscriptionrights.security.jwt.JwtService;
import com.alwaysmoveforward.subscriptionrights.services.ApiTokenService;
import com.alwaysmoveforward.subscriptionrights.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService, UserService userService,
                                                     ApiTokenService apiTokenService, CorsConfigurationSource corsConfigurationSource)
            throws Exception {
        http
                // Stateless cookie+JWT auth, no server-rendered forms -- CSRF tokens would
                // require a session to store them against, which this design deliberately has none of.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthenticatedEntryPoint()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/callback", "/api/auth/logout").permitAll()
                        // No cookie-session gate for these -- each route's own
                        // @externalApiTokenAccessGuard.canAccess(...) check (see
                        // ExternalApiTokenAccessGuard) is what actually enforces that the caller
                        // holds a valid API token scoped to the requested application.
                        .requestMatchers("/api/external/**").permitAll()
                        .anyRequest().authenticated())
                // Either filter may authenticate a request -- a browser session cookie or a
                // machine "Authorization: Bearer <token>" header -- whichever finds its
                // credential first; each is a no-op once the other has already authenticated.
                .addFilterBefore(new JwtCookieAuthenticationFilter(jwtService, userService),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ApiTokenAuthenticationFilter(apiTokenService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private AuthenticationEntryPoint unauthenticatedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
