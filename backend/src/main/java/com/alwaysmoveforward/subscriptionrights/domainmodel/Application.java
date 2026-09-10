package com.alwaysmoveforward.subscriptionrights.domainmodel;

import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Aggregate root for an application that owns subscription tiers, subscription
 * rights, and the grants linking them.
 */
public class Application {

    private static final int EXTERNAL_ID_MAX_LENGTH = 20;
    private static final Pattern EXTERNAL_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

    private final Long id;
    private String name;
    private String externalId;
    private String description;
    private final Instant createdAt;

    private Application(Long id, String name, String externalId, String description, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Application create(String name, String externalId, String description) {
        return new Application(null, requireName(name), requireExternalId(externalId), description, Instant.now());
    }

    /**
     * Reconstitutes an Application from persisted state. Only mappers should call this.
     */
    public static Application reconstitute(Long id, String name, String externalId, String description, Instant createdAt) {
        return new Application(id, name, externalId, description, createdAt);
    }

    public void rename(String newName) {
        this.name = requireName(newName);
    }

    public void changeExternalId(String newExternalId) {
        this.externalId = requireExternalId(newExternalId);
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Application name must not be blank");
        }
        return name;
    }

    /**
     * ExternalId is what a machine caller puts in the URL in place of the numeric Id (see
     * /api/external/applications/{externalId}/...), so it's restricted to characters that never
     * need percent-encoding and kept short enough to stay readable in a path.
     */
    private static String requireExternalId(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            throw new DomainException("Application externalId must not be blank");
        }
        if (externalId.length() > EXTERNAL_ID_MAX_LENGTH) {
            throw new DomainException("Application externalId must be at most " + EXTERNAL_ID_MAX_LENGTH + " characters");
        }
        if (!EXTERNAL_ID_PATTERN.matcher(externalId).matches()) {
            throw new DomainException("Application externalId may only contain letters, numbers, hyphens, and underscores");
        }
        return externalId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
