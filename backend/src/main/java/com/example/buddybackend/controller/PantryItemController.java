package com.example.buddybackend.controller;

import com.example.buddybackend.dto.PantryItemCreateRequest;
import com.example.buddybackend.dto.PantryItemResponse;
import com.example.buddybackend.dto.PantryItemUpdateRequest;
import com.example.buddybackend.entity.PantryItem;
import com.example.buddybackend.service.PantryItemService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pantry")
public class PantryItemController {

    private final PantryItemService service;

    public PantryItemController(PantryItemService service) {
        this.service = service;
    }

    @PostMapping("/household/{householdId}")
    public ResponseEntity<PantryItemResponse> create(
            @PathVariable UUID householdId,
            @RequestBody PantryItemCreateRequest request
    ) {
        PantryItem item = new PantryItem();

        item.setHouseholdId(householdId);
        item.setStorageZoneId(request.getStorageZoneId());
        item.setCategoryId(request.getCategoryId());
        item.setName(request.getName());
        item.setSubtitle(request.getSubtitle());
        item.setShelfLocation(request.getShelfLocation());
        item.setBarcode(request.getBarcode());
        item.setPackageSize(request.getPackageSize());
        item.setQuantity(request.getQuantity());
        item.setUnit(request.getUnit());
        item.setExpiryAt(request.getExpiryAt());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setCalories(request.getCalories());
        item.setProteinGrams(request.getProteinGrams());
        item.setCarbsGrams(request.getCarbsGrams());
        item.setFatGrams(request.getFatGrams());
        item.setNutriScore(request.getNutriScore());
        item.setEcoImpact(request.getEcoImpact());

        return ResponseEntity.ok(
                toResponse(service.create(item))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PantryItemResponse> getById(
            @PathVariable UUID id
    ) {
        return service.findById(id)
                .map(item -> ResponseEntity.ok(toResponse(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<PantryItemResponse>> getAll(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findByHouseholdId(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/household/{householdId}/active")
    public ResponseEntity<List<PantryItemResponse>> getActive(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findActiveItems(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/household/{householdId}/favorites")
    public ResponseEntity<List<PantryItemResponse>> getFavorites(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findFavorites(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/household/{householdId}/zone/{zoneId}")
    public ResponseEntity<List<PantryItemResponse>> getByZone(
            @PathVariable UUID householdId,
            @PathVariable UUID zoneId
    ) {
        return ResponseEntity.ok(
                service.findByStorageZone(householdId, zoneId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/household/{householdId}/category/{categoryId}")
    public ResponseEntity<List<PantryItemResponse>> getByCategory(
            @PathVariable UUID householdId,
            @PathVariable UUID categoryId
    ) {
        return ResponseEntity.ok(
                service.findByCategory(householdId, categoryId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PantryItemResponse> update(
            @PathVariable UUID id,
            @RequestBody PantryItemUpdateRequest request
    ) {
        PantryItem item = service.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pantry item not found"));

        item.setStorageZoneId(request.getStorageZoneId());
        item.setCategoryId(request.getCategoryId());
        item.setName(request.getName());
        item.setSubtitle(request.getSubtitle());
        item.setShelfLocation(request.getShelfLocation());
        item.setPackageSize(request.getPackageSize());
        item.setQuantity(request.getQuantity());
        item.setUnit(request.getUnit());
        item.setExpiryAt(request.getExpiryAt());
        item.setIsConsumed(request.getIsConsumed());
        item.setIsFavorite(request.getIsFavorite());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());

        return ResponseEntity.ok(
                toResponse(service.update(item))
        );
    }

    @PatchMapping("/{id}/consume")
    public ResponseEntity<PantryItemResponse> consume(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                toResponse(service.consume(id))
        );
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<PantryItemResponse> toggleFavorite(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                toResponse(service.toggleFavorite(id))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PantryItemResponse toResponse(PantryItem item) {
        PantryItemResponse response = new PantryItemResponse();

        response.setId(item.getId());
        response.setHouseholdId(item.getHouseholdId());
        response.setStorageZoneId(item.getStorageZoneId());
        response.setCategoryId(item.getCategoryId());
        response.setName(item.getName());
        response.setSubtitle(item.getSubtitle());
        response.setShelfLocation(item.getShelfLocation());
        response.setBarcode(item.getBarcode());
        response.setPackageSize(item.getPackageSize());
        response.setQuantity(item.getQuantity());
        response.setUnit(item.getUnit());
        response.setExpiryAt(item.getExpiryAt());
        response.setStockedAt(item.getStockedAt());
        response.setIsConsumed(item.getIsConsumed());
        response.setIsFavorite(item.getIsFavorite());
        response.setConfidenceScore(item.getConfidenceScore());
        response.setPrice(item.getPrice());
        response.setImageUrl(item.getImageUrl());
        response.setCalories(item.getCalories());
        response.setProteinGrams(item.getProteinGrams());
        response.setCarbsGrams(item.getCarbsGrams());
        response.setFatGrams(item.getFatGrams());
        response.setNutriScore(item.getNutriScore());
        response.setEcoImpact(item.getEcoImpact());
        response.setSyncStatus(item.getSyncStatus());
        response.setCreatedByUserId(item.getCreatedByUserId());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());

        return response;
    }
}