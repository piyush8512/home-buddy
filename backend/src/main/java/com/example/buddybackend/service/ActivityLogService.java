package com.example.buddybackend.service;

import com.example.buddybackend.entity.ActivityLog;
import com.example.buddybackend.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository repository;

    public ActivityLogService(ActivityLogRepository repository) {
        this.repository = repository;
    }

    public ActivityLog create(ActivityLog activityLog) {
        return repository.save(activityLog);
    }

    public Optional<ActivityLog> findById(UUID id) {
        return repository.findById(id);
    }

    public List<ActivityLog> findByHouseholdId(UUID householdId) {
        return repository.findByHouseholdIdOrderByCreatedAtDesc(
                householdId
        );
    }

    public List<ActivityLog> findByUserId(UUID userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(
                userId
        );
    }

    public List<ActivityLog> findByEntityId(UUID entityId) {
        return repository.findByEntityId(entityId);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}