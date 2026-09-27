package com.example.buddybackend.repository;

import com.example.buddybackend.entity.ScanLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScanLogRepository extends JpaRepository<ScanLog, UUID> {

    List<ScanLog> findByHouseholdId(UUID householdId);

    List<ScanLog> findByUserId(UUID userId);

    List<ScanLog> findByBarcode(String barcode);
}