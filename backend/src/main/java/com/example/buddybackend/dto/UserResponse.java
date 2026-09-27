package com.example.buddybackend.dto;

import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String firebaseUid;
    private String email;
    private String displayName;
    private String avatarUrl;
    private Boolean isActive;

    public UserResponse() {
    }

    public UserResponse(
            UUID id,
            String firebaseUid,
            String email,
            String displayName,
            String avatarUrl,
            Boolean isActive
    ) {
        this.id = id;
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}