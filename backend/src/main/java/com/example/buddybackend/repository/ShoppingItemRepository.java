package com.example.buddybackend.repository;

import com.example.buddybackend.entity.ShoppingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShoppingItemRepository
        extends JpaRepository<ShoppingItem, UUID> {

    List<ShoppingItem> findByHouseholdId(UUID householdId);

    List<ShoppingItem> findByHouseholdIdAndIsCheckedFalse(UUID householdId);

    List<ShoppingItem> findByHouseholdIdAndIsCheckedTrue(UUID householdId);

    List<ShoppingItem> findByHouseholdIdAndCategoryId(
            UUID householdId,
            UUID categoryId
    );
}