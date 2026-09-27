package com.example.buddybackend.repository;

import com.example.buddybackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserId(UUID userId);

    List<Notification> findByUserIdAndIsReadFalse(UUID userId);

    List<Notification> findByHouseholdId(UUID householdId);

    List<Notification> findByStatus(String status);

    List<Notification> findByPantryItemId(UUID pantryItemId);
}