package com.example.pantry.model;

public class PantryItemDto {
    private String id;
    private String name;
    private String category;
    private String packageSize;
    private String barcode;
    private long expiryDateMillis;
    private String storageZone;
    private int quantity;
    private String unit;
    private String imageUrl;
    private double price;
    private boolean isConsumed;
    private long updatedAtMillis;
    private String householdId;

    public PantryItemDto() {}

    public PantryItemDto(String id, String name, String category, String packageSize, String barcode,
                         long expiryDateMillis, String storageZone, int quantity, String unit,
                         String imageUrl, double price, boolean isConsumed, long updatedAtMillis,
                         String householdId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.packageSize = packageSize;
        this.barcode = barcode;
        this.expiryDateMillis = expiryDateMillis;
        this.storageZone = storageZone;
        this.quantity = quantity;
        this.unit = unit;
        this.imageUrl = imageUrl;
        this.price = price;
        this.isConsumed = isConsumed;
        this.updatedAtMillis = updatedAtMillis;
        this.householdId = householdId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPackageSize() { return packageSize; }
    public void setPackageSize(String packageSize) { this.packageSize = packageSize; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public long getExpiryDateMillis() { return expiryDateMillis; }
    public void setExpiryDateMillis(long expiryDateMillis) { this.expiryDateMillis = expiryDateMillis; }

    public String getStorageZone() { return storageZone; }
    public void setStorageZone(String storageZone) { this.storageZone = storageZone; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isConsumed() { return isConsumed; }
    public void setConsumed(boolean consumed) { isConsumed = consumed; }

    public long getUpdatedAtMillis() { return updatedAtMillis; }
    public void setUpdatedAtMillis(long updatedAtMillis) { this.updatedAtMillis = updatedAtMillis; }

    public String getHouseholdId() { return householdId; }
    public void setHouseholdId(String householdId) { this.householdId = householdId; }
}
