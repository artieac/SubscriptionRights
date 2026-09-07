package com.alwaysmoveforward.subscriptionrights.domainmodel;

/**
 * The subset of an Auth0 /userinfo response this system cares about.
 */
public class Auth0UserProfile {

    private final String subject;
    private final String email;
    private final String name;
    private final String locale;

    public Auth0UserProfile(String subject, String email, String name, String locale) {
        this.subject = subject;
        this.email = email;
        this.name = name;
        this.locale = locale;
    }

    public String getSubject() {
        return subject;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getLocale() {
        return locale;
    }
}
