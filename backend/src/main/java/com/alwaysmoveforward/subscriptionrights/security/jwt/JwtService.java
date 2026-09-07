package com.alwaysmoveforward.subscriptionrights.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * Issues and validates the signed JWT stored in the session cookie. The token's only
 * payload is the application's own numeric User id -- everything else about the user
 * is looked up from the database on each request.
 */
@Service
public class JwtService {

    private static final String USER_ID_CLAIM = "uid";

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String issueToken(Long userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.getCookieExpirationMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .claim(USER_ID_CLAIM, userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(jwtProperties.toSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<Long> parseUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.toSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Number userId = claims.get(USER_ID_CLAIM, Number.class);
            return userId == null ? Optional.empty() : Optional.of(userId.longValue());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public String cookieName() {
        return jwtProperties.getCookieName();
    }

    public long expirationSeconds() {
        return jwtProperties.getCookieExpirationMinutes() * 60;
    }

    public String cookieDomain() {
        return jwtProperties.getCookieDomain();
    }

    public boolean cookieSecure() {
        return jwtProperties.isCookieSecure();
    }
}
