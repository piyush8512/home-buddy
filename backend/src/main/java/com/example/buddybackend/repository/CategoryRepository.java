package com.example.buddybackend.repository;

import com.example.buddybackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByHouseholdId(UUID householdId);

    List<Category> findByHouseholdIdIsNull();

    List<Category> findByHouseholdIdOrHouseholdIdIsNull(UUID householdId);
}