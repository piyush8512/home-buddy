package com.example.buddybackend.dto;

public class StorageZoneRequest {

    private String name;
    private String zoneType;
    private String icon;
    private Integer displayOrder;

    public StorageZoneRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZoneType() {
        return zoneType;
    }

    public void setZoneType(String zoneType) {
        this.zoneType = zoneType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}