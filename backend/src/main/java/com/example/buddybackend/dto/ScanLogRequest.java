package com.example.buddybackend.dto;

public class ScanLogRequest {

    private String scanType;
    private String barcode;
    private String rawOcrText;
    private String recognizedName;
    private Integer confidenceScore;
    private String status;

    public ScanLogRequest() {
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getRawOcrText() {
        return rawOcrText;
    }

    public void setRawOcrText(String rawOcrText) {
        this.rawOcrText = rawOcrText;
    }

    public String getRecognizedName() {
        return recognizedName;
    }

    public void setRecognizedName(String recognizedName) {
        this.recognizedName = recognizedName;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}