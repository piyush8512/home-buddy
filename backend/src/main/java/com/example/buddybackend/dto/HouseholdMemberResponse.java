package com.example.buddybackend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class HouseholdMemberResponse {

    private UUID id;
    private UUID householdId;
    private UUID userId;
    private String role;
    private OffsetDateTime joinedAt;

    public HouseholdMemberResponse() {
    }

    public HouseholdMemberResponse(
            UUID id,
            UUID householdId,
            UUID userId,
            String role,
            OffsetDateTime joinedAt
    ) {
        this.id = id;
        this.householdId = householdId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public OffsetDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(OffsetDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}