package com.example.buddybackend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PantryItemUpdateRequest {

    private UUID storageZoneId;
    private UUID categoryId;
    private String name;
    private String subtitle;
    private String shelfLocation;
    private String packageSize;
    private Integer quantity;
    private String unit;
    private OffsetDateTime expiryAt;
    private Boolean isConsumed;
    private Boolean isFavorite;
    private BigDecimal price;
    private String imageUrl;

    public PantryItemUpdateRequest() {
    }

    public UUID getStorageZoneId() {
        return storageZoneId;
    }

    public void setStorageZoneId(UUID storageZoneId) {
        this.storageZoneId = storageZoneId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public String getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(String packageSize) {
        this.packageSize = packageSize;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public OffsetDateTime getExpiryAt() {
        return expiryAt;
    }

    public void setExpiryAt(OffsetDateTime expiryAt) {
        this.expiryAt = expiryAt;
    }

    public Boolean getIsConsumed() {
        return isConsumed;
    }

    public void setIsConsumed(Boolean isConsumed) {
        this.isConsumed = isConsumed;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}