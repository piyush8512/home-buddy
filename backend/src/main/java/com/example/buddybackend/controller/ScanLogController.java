package com.example.buddybackend.controller;

import com.example.buddybackend.dto.ScanLogRequest;
import com.example.buddybackend.dto.ScanLogResponse;
import com.example.buddybackend.entity.ScanLog;
import com.example.buddybackend.service.ScanLogService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scans")
public class ScanLogController {

    private final ScanLogService service;

    public ScanLogController(ScanLogService service) {
        this.service = service;
    }

    @PostMapping("/household/{householdId}")
    public ResponseEntity<ScanLogResponse> create(
            @PathVariable UUID householdId,
            @RequestBody ScanLogRequest request
    ) {
        ScanLog scan = new ScanLog();

        scan.setHouseholdId(householdId);
        scan.setScanType(request.getScanType());
        scan.setBarcode(request.getBarcode());
        scan.setRawOcrText(request.getRawOcrText());
        scan.setRecognizedName(request.getRecognizedName());
        scan.setConfidenceScore(request.getConfidenceScore());
        scan.setStatus(request.getStatus());

        return ResponseEntity.ok(
                toResponse(service.create(scan))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScanLogResponse> getById(
            @PathVariable UUID id
    ) {
        return service.findById(id)
                .map(scan -> ResponseEntity.ok(toResponse(scan)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<ScanLogResponse>> getByHousehold(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findByHouseholdId(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ScanLogResponse toResponse(ScanLog scan) {
        ScanLogResponse response = new ScanLogResponse();

        response.setId(scan.getId());
        response.setHouseholdId(scan.getHouseholdId());
        response.setUserId(scan.getUserId());
        response.setScanType(scan.getScanType());
        response.setBarcode(scan.getBarcode());
        response.setRawOcrText(scan.getRawOcrText());
        response.setRecognizedName(scan.getRecognizedName());
        response.setConfidenceScore(scan.getConfidenceScore());
        response.setStatus(scan.getStatus());
        response.setCreatedAt(scan.getCreatedAt());

        return response;
    }
}