package com.example.buddybackend.controller;

import com.example.buddybackend.dto.HouseholdCreateRequest;
import com.example.buddybackend.dto.HouseholdResponse;
import com.example.buddybackend.entity.Household;
import com.example.buddybackend.entity.User;
import com.example.buddybackend.service.HouseholdService;
import com.example.buddybackend.service.UserService;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/households")
public class HouseholdController {

    private final HouseholdService householdService;
    private final UserService userService;

    public HouseholdController(
            HouseholdService householdService,
            UserService userService
    ) {
        this.householdService = householdService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<HouseholdResponse> create(
            Authentication authentication,
            @RequestBody HouseholdCreateRequest request
    ) {
        FirebaseToken token =
                (FirebaseToken) authentication.getCredentials();

        User user = userService.findByFirebaseUid(token.getUid())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Household household = new Household();

        household.setName(request.getName());
        household.setCreatedByUserId(user.getId());

        Household saved = householdService.create(household);

        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HouseholdResponse> getById(
            @PathVariable UUID id
    ) {
        return householdService.findById(id)
                .map(household ->
                        ResponseEntity.ok(toResponse(household)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/invite/{inviteCode}")
    public ResponseEntity<HouseholdResponse> getByInviteCode(
            @PathVariable String inviteCode
    ) {
        return householdService.findByInviteCode(inviteCode)
                .map(household ->
                        ResponseEntity.ok(toResponse(household)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    public ResponseEntity<List<HouseholdResponse>> getMyHouseholds(
            Authentication authentication
    ) {
        FirebaseToken token =
                (FirebaseToken) authentication.getCredentials();

        User user = userService.findByFirebaseUid(token.getUid())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<HouseholdResponse> response =
                householdService
                        .findByCreatedByUserId(user.getId())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HouseholdResponse> update(
            @PathVariable UUID id,
            @RequestBody HouseholdCreateRequest request
    ) {
        Household household = householdService.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Household not found"));

        household.setName(request.getName());

        return ResponseEntity.ok(
                toResponse(householdService.update(household))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        householdService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private HouseholdResponse toResponse(Household household) {
        return new HouseholdResponse(
                household.getId(),
                household.getName(),
                household.getInviteCode(),
                household.getCreatedByUserId(),
                household.getCreatedAt(),
                household.getUpdatedAt()
        );
    }
}