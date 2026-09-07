package com.alwaysmoveforward.subscriptionrights.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Issues and verifies the OAuth "state" parameter used in the Auth0 login flow.
 * The state is a short-lived, self-signed token -- it proves the callback we're
 * handling corresponds to a login we initiated, without needing a server-side session.
 */
@Service
public class LoginStateService {

    private final JwtProperties jwtProperties;

    public LoginStateService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String issueState() {
        Instant now = Instant.now();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(5, ChronoUnit.MINUTES)))
                .signWith(jwtProperties.toSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValid(String state) {
        if (state == null || state.isBlank()) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.toSigningKey())
                    .build()
                    .parseClaimsJws(state);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
