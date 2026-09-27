package com.example.buddybackend.service;

import com.example.buddybackend.entity.ShoppingItem;
import com.example.buddybackend.repository.ShoppingItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ShoppingItemService {

    private final ShoppingItemRepository repository;

    public ShoppingItemService(ShoppingItemRepository repository) {
        this.repository = repository;
    }

    public ShoppingItem create(ShoppingItem item) {
        return repository.save(item);
    }

    public Optional<ShoppingItem> findById(UUID id) {
        return repository.findById(id);
    }

    public List<ShoppingItem> findByHouseholdId(UUID householdId) {
        return repository.findByHouseholdId(householdId);
    }

    public List<ShoppingItem> findUnchecked(UUID householdId) {
        return repository.findByHouseholdIdAndIsCheckedFalse(
                householdId
        );
    }

    public List<ShoppingItem> findChecked(UUID householdId) {
        return repository.findByHouseholdIdAndIsCheckedTrue(
                householdId
        );
    }

    public List<ShoppingItem> findByCategory(
            UUID householdId,
            UUID categoryId
    ) {
        return repository.findByHouseholdIdAndCategoryId(
                householdId,
                categoryId
        );
    }

    public ShoppingItem update(ShoppingItem item) {
        return repository.save(item);
    }

    public ShoppingItem toggleChecked(UUID id) {
        ShoppingItem item = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Shopping item not found"));

        item.setIsChecked(!item.getIsChecked());

        return repository.save(item);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Shopping item not found");
        }

        repository.deleteById(id);
    }
}