package com.example.buddybackend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CategoryResponse {

    private UUID id;
    private UUID householdId;
    private String name;
    private String colorHex;
    private String icon;
    private OffsetDateTime createdAt;

    public CategoryResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(UUID householdId) {
        this.householdId = householdId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}