package com.alwaysmoveforward.subscriptionrights.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Built from ConfigurationSettings (see config/ConfigurationSettingsConfig's adminEmailsProperties
 * bean) -- a comma-separated list of email addresses (subscriptions.config-api.settings'
 * "admin-emails") that are automatically promoted to admin on login, whether that's their first
 * login (see UserService#findOrCreateFromAuth0Profile) or any later one where they aren't already
 * an admin. Comparison is case-insensitive, since email addresses are conventionally treated that
 * way.
 */
public class AdminEmailsProperties {

    private final Set<String> emails;

    public AdminEmailsProperties(String commaSeparatedEmails) {
        this.emails = commaSeparatedEmails == null
                ? Set.of()
                : Arrays.stream(commaSeparatedEmails.split(","))
                        .map(String::trim)
                        .filter(email -> !email.isBlank())
                        .map(String::toLowerCase)
                        .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isAdminEmail(String email) {
        return email != null && emails.contains(email.toLowerCase());
    }
}
