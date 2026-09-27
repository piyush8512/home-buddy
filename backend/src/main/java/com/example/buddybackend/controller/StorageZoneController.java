package com.example.buddybackend.controller;

import com.example.buddybackend.dto.StorageZoneRequest;
import com.example.buddybackend.dto.StorageZoneResponse;
import com.example.buddybackend.entity.StorageZone;
import com.example.buddybackend.service.StorageZoneService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage-zones")
public class StorageZoneController {

    private final StorageZoneService service;

    public StorageZoneController(StorageZoneService service) {
        this.service = service;
    }

    @PostMapping("/household/{householdId}")
    public ResponseEntity<StorageZoneResponse> create(
            @PathVariable UUID householdId,
            @RequestBody StorageZoneRequest request
    ) {
        StorageZone zone = new StorageZone();

        zone.setHouseholdId(householdId);
        zone.setName(request.getName());
        zone.setZoneType(request.getZoneType());
        zone.setIcon(request.getIcon());
        zone.setDisplayOrder(request.getDisplayOrder());

        return ResponseEntity.ok(
                toResponse(service.create(zone))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageZoneResponse> getById(
            @PathVariable UUID id
    ) {
        return service.findById(id)
                .map(zone -> ResponseEntity.ok(toResponse(zone)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<StorageZoneResponse>> getAll(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findByHouseholdId(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<StorageZoneResponse> update(
            @PathVariable UUID id,
            @RequestBody StorageZoneRequest request
    ) {
        StorageZone zone = service.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Storage zone not found"));

        zone.setName(request.getName());
        zone.setZoneType(request.getZoneType());
        zone.setIcon(request.getIcon());
        zone.setDisplayOrder(request.getDisplayOrder());

        return ResponseEntity.ok(
                toResponse(service.update(zone))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private StorageZoneResponse toResponse(StorageZone zone) {
        StorageZoneResponse response = new StorageZoneResponse();

        response.setId(zone.getId());
        response.setHouseholdId(zone.getHouseholdId());
        response.setName(zone.getName());
        response.setZoneType(zone.getZoneType());
        response.setIcon(zone.getIcon());
        response.setDisplayOrder(zone.getDisplayOrder());
        response.setCreatedAt(zone.getCreatedAt());

        return response;
    }
}