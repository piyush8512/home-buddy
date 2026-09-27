package com.example.buddybackend.controller;

import com.example.buddybackend.dto.ActivityLogResponse;
import com.example.buddybackend.entity.ActivityLog;
import com.example.buddybackend.service.ActivityLogService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity")
public class ActivityLogController {

    private final ActivityLogService service;

    public ActivityLogController(ActivityLogService service) {
        this.service = service;
    }

    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<ActivityLogResponse>> getByHousehold(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findByHouseholdId(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityLogResponse>> getByUser(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                service.findByUserId(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private ActivityLogResponse toResponse(
            ActivityLog activity
    ) {
        ActivityLogResponse response = new ActivityLogResponse();

        response.setId(activity.getId());
        response.setHouseholdId(activity.getHouseholdId());
        response.setUserId(activity.getUserId());
        response.setAction(activity.getAction());
        response.setEntityType(activity.getEntityType());
        response.setEntityId(activity.getEntityId());
        response.setDescription(activity.getDescription());
        response.setCreatedAt(activity.getCreatedAt());

        return response;
    }
}