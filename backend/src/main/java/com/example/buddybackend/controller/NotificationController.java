package com.example.buddybackend.controller;

import com.example.buddybackend.dto.NotificationResponse;
import com.example.buddybackend.entity.Notification;
import com.example.buddybackend.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(
            @PathVariable UUID id
    ) {
        return service.findById(id)
                .map(notification ->
                        ResponseEntity.ok(toResponse(notification)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getByUser(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                service.findByUserId(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                service.findUnread(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                toResponse(service.markAsRead(id))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private NotificationResponse toResponse(
            Notification notification
    ) {
        NotificationResponse response = new NotificationResponse();

        response.setId(notification.getId());
        response.setHouseholdId(notification.getHouseholdId());
        response.setUserId(notification.getUserId());
        response.setPantryItemId(notification.getPantryItemId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setAlertType(notification.getAlertType());
        response.setStatus(notification.getStatus());
        response.setIsRead(notification.getIsRead());
        response.setScheduledFor(notification.getScheduledFor());
        response.setSentAt(notification.getSentAt());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }
}