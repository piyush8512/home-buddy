package com.example.buddybackend.service;

import com.example.buddybackend.entity.Notification;
import com.example.buddybackend.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification create(Notification notification) {
        return repository.save(notification);
    }

    public Optional<Notification> findById(UUID id) {
        return repository.findById(id);
    }

    public List<Notification> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    public List<Notification> findUnread(UUID userId) {
        return repository.findByUserIdAndIsReadFalse(userId);
    }

    public List<Notification> findByHouseholdId(UUID householdId) {
        return repository.findByHouseholdId(householdId);
    }

    public List<Notification> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public List<Notification> findByPantryItemId(UUID pantryItemId) {
        return repository.findByPantryItemId(pantryItemId);
    }

    public Notification markAsRead(UUID id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));

        notification.setIsRead(true);

        return repository.save(notification);
    }

    public Notification update(Notification notification) {
        return repository.save(notification);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}