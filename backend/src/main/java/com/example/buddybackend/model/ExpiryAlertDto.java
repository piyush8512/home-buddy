package com.example.buddybackend.model;

public class ExpiryAlertDto {
    private String itemId;
    private String itemName;
    private String storageZone;
    private long daysRemaining;
    private String urgency; // "EXPIRED", "CRITICAL" (<= 2 days), "WARNING" (<= 5 days)

    public ExpiryAlertDto() {}

    public ExpiryAlertDto(String itemId, String itemName, String storageZone, long daysRemaining, String urgency) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.storageZone = storageZone;
        this.daysRemaining = daysRemaining;
        this.urgency = urgency;
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getStorageZone() { return storageZone; }
    public void setStorageZone(String storageZone) { this.storageZone = storageZone; }

    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
}
