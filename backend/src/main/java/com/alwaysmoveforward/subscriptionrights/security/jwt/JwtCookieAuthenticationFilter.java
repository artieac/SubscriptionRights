package com.alwaysmoveforward.subscriptionrights.security.jwt;

import com.alwaysmoveforward.subscriptionrights.domainmodel.User;
import com.alwaysmoveforward.subscriptionrights.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
 * Reads the JWT session cookie on every request and, if it's valid and still points
 * at a real User, populates the Spring Security context. There is no server-side
 * session -- the cookie plus the database lookup is the entire authentication state.
 */
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtCookieAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            readCookie(request, jwtService.cookieName())
                    .flatMap(jwtService::parseUserId)
                    .flatMap(this::loadUser)
                    .ifPresent(user -> SecurityContextHolder.getContext().setAuthentication(toAuthentication(user)));
        }
        filterChain.doFilter(request, response);
    }

    private Optional<User> loadUser(Long userId) {
        try {
            return Optional.of(userService.getUser(userId));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private UsernamePasswordAuthenticationToken toAuthentication(User user) {
        List<GrantedAuthority> authorities = user.isAdmin()
                ? List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                user.getId(), user.getEmail(), user.getDisplayName(), user.isAdmin());
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    private Optional<String> readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }
}
