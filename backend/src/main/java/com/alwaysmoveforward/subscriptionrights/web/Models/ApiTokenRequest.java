package com.alwaysmoveforward.subscriptionrights.web.Models;

import jakarta.validation.constraints.NotBlank;

public class ApiTokenRequest {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
