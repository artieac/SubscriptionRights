package com.alwaysmoveforward.subscriptionrights.security.apitoken;

import com.alwaysmoveforward.subscriptionrights.domainmodel.ApiToken;
import com.alwaysmoveforward.subscriptionrights.services.ApiTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Reads an "Authorization: Bearer <token>" header on every request and, if it names a live
 * (non-revoked) ApiToken, populates the Spring Security context with an ApiClientPrincipal.
 * Coexists with JwtCookieAuthenticationFilter -- each only acts on its own credential source and
 * only if the request isn't already authenticated, so a browser session cookie and a machine
 * Bearer token are handled independently by whichever filter finds its credential first.
 */
public class ApiTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final ApiTokenService apiTokenService;

    public ApiTokenAuthenticationFilter(ApiTokenService apiTokenService) {
        this.apiTokenService = apiTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            readBearerToken(request)
                    .flatMap(apiTokenService::authenticate)
                    .ifPresent(apiToken -> SecurityContextHolder.getContext().setAuthentication(toAuthentication(apiToken)));
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken toAuthentication(ApiToken apiToken) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"));
        ApiClientPrincipal principal = new ApiClientPrincipal(apiToken.getId(), apiToken.getApplicationId());
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    private Optional<String> readBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()).trim());
        }
        return Optional.empty();
    }
}
