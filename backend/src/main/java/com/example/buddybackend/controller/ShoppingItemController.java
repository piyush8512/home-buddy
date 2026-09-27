package com.example.buddybackend.controller;

import com.example.buddybackend.dto.ShoppingItemCreateRequest;
import com.example.buddybackend.dto.ShoppingItemResponse;
import com.example.buddybackend.dto.ShoppingItemUpdateRequest;
import com.example.buddybackend.entity.ShoppingItem;
import com.example.buddybackend.service.ShoppingItemService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingItemController {

    private final ShoppingItemService service;

    public ShoppingItemController(ShoppingItemService service) {
        this.service = service;
    }

    @PostMapping("/household/{householdId}")
    public ResponseEntity<ShoppingItemResponse> create(
            @PathVariable UUID householdId,
            @RequestBody ShoppingItemCreateRequest request
    ) {
        ShoppingItem item = new ShoppingItem();

        item.setHouseholdId(householdId);
        item.setName(request.getName());
        item.setSubtitle(request.getSubtitle());
        item.setCategoryId(request.getCategoryId());
        item.setStore(request.getStore());
        item.setIsAutoDepleted(request.getIsAutoDepleted());
        item.setDepletionPercent(request.getDepletionPercent());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());

        return ResponseEntity.ok(
                toResponse(service.create(item))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingItemResponse> getById(
            @PathVariable UUID id
    ) {
        return service.findById(id)
                .map(item -> ResponseEntity.ok(toResponse(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<ShoppingItemResponse>> getAll(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findByHouseholdId(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/household/{householdId}/unchecked")
    public ResponseEntity<List<ShoppingItemResponse>> getUnchecked(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findUnchecked(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/household/{householdId}/checked")
    public ResponseEntity<List<ShoppingItemResponse>> getChecked(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findChecked(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShoppingItemResponse> update(
            @PathVariable UUID id,
            @RequestBody ShoppingItemUpdateRequest request
    ) {
        ShoppingItem item = service.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Shopping item not found"));

        item.setName(request.getName());
        item.setSubtitle(request.getSubtitle());
        item.setCategoryId(request.getCategoryId());
        item.setStore(request.getStore());
        item.setIsAutoDepleted(request.getIsAutoDepleted());
        item.setDepletionPercent(request.getDepletionPercent());
        item.setIsChecked(request.getIsChecked());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());

        return ResponseEntity.ok(
                toResponse(service.update(item))
        );
    }

    @PatchMapping("/{id}/check")
    public ResponseEntity<ShoppingItemResponse> toggleChecked(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                toResponse(service.toggleChecked(id))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ShoppingItemResponse toResponse(ShoppingItem item) {
        ShoppingItemResponse response = new ShoppingItemResponse();

        response.setId(item.getId());
        response.setHouseholdId(item.getHouseholdId());
        response.setName(item.getName());
        response.setSubtitle(item.getSubtitle());
        response.setCategoryId(item.getCategoryId());
        response.setStore(item.getStore());
        response.setIsAutoDepleted(item.getIsAutoDepleted());
        response.setDepletionPercent(item.getDepletionPercent());
        response.setIsChecked(item.getIsChecked());
        response.setPrice(item.getPrice());
        response.setAddedByUserId(item.getAddedByUserId());
        response.setImageUrl(item.getImageUrl());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());

        return response;
    }
}