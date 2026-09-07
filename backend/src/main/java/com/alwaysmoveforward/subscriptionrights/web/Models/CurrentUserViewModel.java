package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.domainmodel.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentUserViewModel {

    private final Long id;
    private final String email;
    private final String displayName;
    private final boolean admin;
    private final String timeZone;
    private final String locale;

    public CurrentUserViewModel(Long id, String email, String displayName, boolean admin, String timeZone, String locale) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.admin = admin;
        this.timeZone = timeZone;
        this.locale = locale;
    }

    public static CurrentUserViewModel from(User user) {
        return new CurrentUserViewModel(user.getId(), user.getEmail(), user.getDisplayName(), user.isAdmin(),
                user.getTimeZone(), user.getLocale());
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

    public String getTimeZone() {
        return timeZone;
    }

    public String getLocale() {
        return locale;
    }
}
