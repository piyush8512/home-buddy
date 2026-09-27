package com.example.buddybackend.repository;

import com.example.buddybackend.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository
        extends JpaRepository<ActivityLog, UUID> {

    List<ActivityLog> findByHouseholdIdOrderByCreatedAtDesc(
            UUID householdId
    );

    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(
            UUID userId
    );

    List<ActivityLog> findByEntityId(UUID entityId);
}