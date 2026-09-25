package com.example.buddybackend.controller;

import com.example.buddybackend.model.ExpiryAlertDto;
import com.example.buddybackend.model.PantryItemDto;
import com.example.buddybackend.service.PantryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pantry")
@CrossOrigin(origins = "*")
public class PantryController {

    @Autowired
    private PantryService pantryService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Pantry Spring Boot Backend API",
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/households/{householdId}/items")
    public ResponseEntity<List<PantryItemDto>> getItems(@PathVariable String householdId) {
        List<PantryItemDto> items = pantryService.getPantryItems(householdId);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/households/{householdId}/items")
    public ResponseEntity<PantryItemDto> saveItem(
            @PathVariable String householdId,
            @RequestBody PantryItemDto itemDto) {
        PantryItemDto saved = pantryService.savePantryItem(householdId, itemDto);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/households/{householdId}/items/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteItem(
            @PathVariable String householdId,
            @PathVariable String itemId) {
        boolean success = pantryService.deletePantryItem(householdId, itemId);
        return ResponseEntity.ok(Map.of("deleted", success, "itemId", itemId));
    }

    @GetMapping("/households/{householdId}/alerts")
    public ResponseEntity<List<ExpiryAlertDto>> getExpiryAlerts(@PathVariable String householdId) {
        List<ExpiryAlertDto> alerts = pantryService.calculateExpiryAlerts(householdId);
        return ResponseEntity.ok(alerts);
    }
}
