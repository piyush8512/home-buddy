package com.example.buddybackend.controller;

import com.example.buddybackend.dto.HouseholdMemberResponse;
import com.example.buddybackend.entity.HouseholdMember;
import com.example.buddybackend.service.HouseholdMemberService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/household-members")
public class HouseholdMemberController {

    private final HouseholdMemberService service;

    public HouseholdMemberController(
            HouseholdMemberService service
    ) {
        this.service = service;
    }

    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<HouseholdMemberResponse>> getMembers(
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
    public ResponseEntity<List<HouseholdMemberResponse>> getUserMemberships(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                service.findByUserId(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkMembership(
            @RequestParam UUID householdId,
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(
                service.isMember(householdId, userId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private HouseholdMemberResponse toResponse(
            HouseholdMember member
    ) {
        return new HouseholdMemberResponse(
                member.getId(),
                member.getHouseholdId(),
                member.getUserId(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}