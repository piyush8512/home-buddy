package com.example.buddybackend.repository;

import com.example.buddybackend.entity.HouseholdMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseholdMemberRepository
        extends JpaRepository<HouseholdMember, UUID> {

    List<HouseholdMember> findByUserId(UUID userId);

    List<HouseholdMember> findByHouseholdId(UUID householdId);

    Optional<HouseholdMember> findByHouseholdIdAndUserId(
            UUID householdId,
            UUID userId
    );

    boolean existsByHouseholdIdAndUserId(
            UUID householdId,
            UUID userId
    );
}