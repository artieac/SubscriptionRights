package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.domainmodel.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class UserViewModel {

    private final Long id;
    private final String email;
    private final String displayName;
    private final boolean admin;
    private final Instant createdAt;

    public UserViewModel(Long id, String email, String displayName, boolean admin, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.admin = admin;
        this.createdAt = createdAt;
    }

    public static UserViewModel from(User user) {
        return new UserViewModel(user.getId(), user.getEmail(), user.getDisplayName(), user.isAdmin(),
                user.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("isAdmin")
    public boolean isAdmin() {
        return admin;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
