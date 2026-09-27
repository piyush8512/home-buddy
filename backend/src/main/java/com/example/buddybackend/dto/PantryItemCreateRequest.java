package com.example.buddybackend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PantryItemCreateRequest {

    private UUID storageZoneId;
    private UUID categoryId;
    private String name;
    private String subtitle;
    private String shelfLocation;
    private String barcode;
    private String packageSize;
    private Integer quantity;
    private String unit;
    private OffsetDateTime expiryAt;
    private BigDecimal price;
    private String imageUrl;
    private Integer calories;
    private BigDecimal proteinGrams;
    private BigDecimal carbsGrams;
    private BigDecimal fatGrams;
    private String nutriScore;
    private String ecoImpact;

    public PantryItemCreateRequest() {
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public BigDecimal getProteinGrams() {
        return proteinGrams;
    }

    public void setProteinGrams(BigDecimal proteinGrams) {
        this.proteinGrams = proteinGrams;
    }

    public BigDecimal getCarbsGrams() {
        return carbsGrams;
    }

    public void setCarbsGrams(BigDecimal carbsGrams) {
        this.carbsGrams = carbsGrams;
    }

    public BigDecimal getFatGrams() {
        return fatGrams;
    }

    public void setFatGrams(BigDecimal fatGrams) {
        this.fatGrams = fatGrams;
    }

    public String getNutriScore() {
        return nutriScore;
    }

    public void setNutriScore(String nutriScore) {
        this.nutriScore = nutriScore;
    }

    public String getEcoImpact() {
        return ecoImpact;
    }

    public void setEcoImpact(String ecoImpact) {
        this.ecoImpact = ecoImpact;
    }
}