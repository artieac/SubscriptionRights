package com.alwaysmoveforward.subscriptionrights.web.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ApplicationRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "may only contain letters, numbers, hyphens, and underscores")
    private String externalId;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
