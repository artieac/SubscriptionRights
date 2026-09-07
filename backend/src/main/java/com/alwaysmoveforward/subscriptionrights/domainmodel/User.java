package com.alwaysmoveforward.subscriptionrights.domainmodel;

import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root for a logged-in user, provisioned automatically from their
 * Auth0 identity on first login.
 */
public class User {

    private final Long id;
    private final String identityProviderSubject;
    private String email;
    private String displayName;
    private boolean isAdmin;
    private final Instant createdAt;
    private Instant updatedAt;
    private String timeZone;
    private String locale;

    private User(Long id, String identityProviderSubject, String email, String displayName, boolean isAdmin,
                 Instant createdAt, Instant updatedAt, String timeZone, String locale) {
        this.id = id;
        this.identityProviderSubject = identityProviderSubject;
        this.email = email;
        this.displayName = displayName;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.timeZone = timeZone;
        this.locale = locale;
    }

    /**
     * Provisions a brand-new User the first time an Auth0 identity is seen.
     */
    public static User provision(Auth0UserProfile profile) {
        if (profile.getSubject() == null || profile.getSubject().isBlank()) {
            throw new DomainException("Cannot provision a User without an identity provider subject");
        }
        if (profile.getEmail() == null || profile.getEmail().isBlank()) {
            throw new DomainException("Cannot provision a User without an email");
        }
        Instant now = Instant.now();
        String displayName = (profile.getName() != null && !profile.getName().isBlank())
                ? profile.getName()
                : profile.getEmail();
        return new User(null, profile.getSubject(), profile.getEmail(), displayName, false,
                now, now, null, profile.getLocale());
    }

    /**
     * Reconstitutes a User from persisted state. Only mappers should call this.
     */
    public static User reconstitute(Long id, String identityProviderSubject, String email, String displayName,
                                     boolean isAdmin, Instant createdAt, Instant updatedAt, String timeZone,
                                     String locale) {
        return new User(id, identityProviderSubject, email, displayName, isAdmin, createdAt, updatedAt, timeZone, locale);
    }

    /**
     * Keeps the locally-stored profile in sync with what Auth0 reports on each login.
     */
    public void refreshFromAuth0Profile(Auth0UserProfile profile) {
        boolean changed = false;
        if (profile.getName() != null && !profile.getName().isBlank() && !Objects.equals(this.displayName, profile.getName())) {
            this.displayName = profile.getName();
            changed = true;
        }
        if (profile.getEmail() != null && !profile.getEmail().isBlank() && !Objects.equals(this.email, profile.getEmail())) {
            this.email = profile.getEmail();
            changed = true;
        }
        if (changed) {
            this.updatedAt = Instant.now();
        }
    }

    public void updatePreferences(String timeZone, String locale) {
        this.timeZone = timeZone;
        this.locale = locale;
        this.updatedAt = Instant.now();
    }

    public void promoteToAdmin() {
        this.isAdmin = true;
        this.updatedAt = Instant.now();
    }

    public void revokeAdmin() {
        this.isAdmin = false;
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getIdentityProviderSubject() {
        return identityProviderSubject;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getLocale() {
        return locale;
    }
}
