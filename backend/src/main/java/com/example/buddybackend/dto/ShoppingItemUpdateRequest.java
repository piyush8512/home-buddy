package com.example.buddybackend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ShoppingItemUpdateRequest {

    private String name;
    private String subtitle;
    private UUID categoryId;
    private String store;
    private Boolean isAutoDepleted;
    private Integer depletionPercent;
    private Boolean isChecked;
    private BigDecimal price;
    private String imageUrl;

    public ShoppingItemUpdateRequest() {
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

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Boolean getIsAutoDepleted() {
        return isAutoDepleted;
    }

    public void setIsAutoDepleted(Boolean isAutoDepleted) {
        this.isAutoDepleted = isAutoDepleted;
    }

    public Integer getDepletionPercent() {
        return depletionPercent;
    }

    public void setDepletionPercent(Integer depletionPercent) {
        this.depletionPercent = depletionPercent;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
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