package com.example.buddybackend.service;

import com.example.buddybackend.entity.ScanLog;
import com.example.buddybackend.repository.ScanLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ScanLogService {

    private final ScanLogRepository repository;

    public ScanLogService(ScanLogRepository repository) {
        this.repository = repository;
    }

    public ScanLog create(ScanLog scanLog) {
        return repository.save(scanLog);
    }

    public Optional<ScanLog> findById(UUID id) {
        return repository.findById(id);
    }

    public List<ScanLog> findByHouseholdId(UUID householdId) {
        return repository.findByHouseholdId(householdId);
    }

    public List<ScanLog> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    public List<ScanLog> findByBarcode(String barcode) {
        return repository.findByBarcode(barcode);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}