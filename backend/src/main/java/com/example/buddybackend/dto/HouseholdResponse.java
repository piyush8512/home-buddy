package com.example.buddybackend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class HouseholdResponse {

    private UUID id;
    private String name;
    private String inviteCode;
    private UUID createdByUserId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public HouseholdResponse() {
    }

    public HouseholdResponse(
            UUID id,
            String name,
            String inviteCode,
            UUID createdByUserId,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdByUserId = createdByUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(UUID createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}