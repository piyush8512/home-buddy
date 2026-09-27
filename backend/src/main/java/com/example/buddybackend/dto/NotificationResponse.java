package com.example.buddybackend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class NotificationResponse {

    private UUID id;
    private UUID householdId;
    private UUID userId;
    private UUID pantryItemId;
    private String title;
    private String message;
    private String alertType;
    private String status;
    private Boolean isRead;
    private OffsetDateTime scheduledFor;
    private OffsetDateTime sentAt;
    private OffsetDateTime createdAt;

    public NotificationResponse() {
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

    public UUID getPantryItemId() {
        return pantryItemId;
    }

    public void setPantryItemId(UUID pantryItemId) {
        this.pantryItemId = pantryItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public OffsetDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(OffsetDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(OffsetDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}