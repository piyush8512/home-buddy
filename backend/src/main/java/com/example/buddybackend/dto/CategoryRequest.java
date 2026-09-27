package com.example.buddybackend.dto;

public class CategoryRequest {

    private String name;
    private String colorHex;
    private String icon;

    public CategoryRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}