package com.example.buddybackend.repository;

import com.example.buddybackend.entity.PantryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PantryItemRepository extends JpaRepository<PantryItem, UUID> {

    List<PantryItem> findByHouseholdId(UUID householdId);
}