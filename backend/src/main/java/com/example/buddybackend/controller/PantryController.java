package com.example.buddybackend.controller;

import com.example.buddybackend.entity.PantryItem;
import com.example.buddybackend.entity.User;
import com.example.buddybackend.service.PantryService;
import com.example.buddybackend.service.UserService;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pantry")
public class PantryController {

    private final PantryService pantryService;
    private final UserService userService;

    public PantryController(
            PantryService pantryService,
            UserService userService
    ) {
        this.pantryService = pantryService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<PantryItem>> getMyItems(
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);

        return ResponseEntity.ok(
                pantryService.findByUserId(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PantryItem> getItem(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);

        return pantryService.findById(id)
                .filter(item -> item.getUserId().equals(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PantryItem> createItem(
            @RequestBody PantryItem item,
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);

        item.setId(null);
        item.setUserId(userId);

        return ResponseEntity.ok(
                pantryService.create(item)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PantryItem> updateItem(
            @PathVariable UUID id,
            @RequestBody PantryItem updatedItem,
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);

        return pantryService.findById(id)
                .filter(item -> item.getUserId().equals(userId))
                .map(item -> {

                    item.setName(updatedItem.getName());
                    item.setQuantity(updatedItem.getQuantity());
                    item.setPrice(updatedItem.getPrice());
                    item.setExpiryDate(updatedItem.getExpiryDate());
                    item.setCategoryId(updatedItem.getCategoryId());
                    item.setStorageZoneId(updatedItem.getStorageZoneId());

                    return ResponseEntity.ok(
                            pantryService.update(item)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);

        return pantryService.findById(id)
                .filter(item -> item.getUserId().equals(userId))
                .map(item -> {
                    pantryService.delete(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private UUID getUserId(Authentication authentication) {

        FirebaseToken token =
                (FirebaseToken) authentication.getCredentials();

        User user = userService
                .findByFirebaseUid(token.getUid())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return user.getId();
    }
}