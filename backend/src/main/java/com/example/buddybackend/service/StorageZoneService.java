package com.example.buddybackend.service;

import com.example.buddybackend.entity.StorageZone;
import com.example.buddybackend.repository.StorageZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class StorageZoneService {

    private final StorageZoneRepository repository;

    public StorageZoneService(StorageZoneRepository repository) {
        this.repository = repository;
    }

    public StorageZone create(StorageZone zone) {
        return repository.save(zone);
    }

    public Optional<StorageZone> findById(UUID id) {
        return repository.findById(id);
    }

    public List<StorageZone> findByHouseholdId(UUID householdId) {
        return repository.findByHouseholdIdOrderByDisplayOrderAsc(
                householdId
        );
    }

    public StorageZone update(StorageZone zone) {
        return repository.save(zone);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}