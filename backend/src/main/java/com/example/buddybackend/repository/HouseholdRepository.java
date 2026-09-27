package com.example.buddybackend.repository;

import com.example.buddybackend.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HouseholdRepository extends JpaRepository<Household, UUID> {

    Optional<Household> findByInviteCode(String inviteCode);
}