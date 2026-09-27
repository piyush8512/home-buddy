package com.example.buddybackend.service;

import com.example.buddybackend.entity.PantryItem;
import com.example.buddybackend.repository.PantryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PantryItemService {

    private final PantryItemRepository repository;

    public PantryItemService(PantryItemRepository repository) {
        this.repository = repository;
    }

    public PantryItem create(PantryItem item) {
        return repository.save(item);
    }

    public Optional<PantryItem> findById(UUID id) {
        return repository.findById(id);
    }

    public List<PantryItem> findByHouseholdId(UUID householdId) {
        return repository.findByHouseholdId(householdId);
    }

    public List<PantryItem> findActiveItems(UUID householdId) {
        return repository.findByHouseholdIdAndIsConsumedFalse(
                householdId
        );
    }

    public List<PantryItem> findFavorites(UUID householdId) {
        return repository.findByHouseholdIdAndIsFavoriteTrue(
                householdId
        );
    }

    public List<PantryItem> findByStorageZone(
            UUID householdId,
            UUID storageZoneId
    ) {
        return repository.findByHouseholdIdAndStorageZoneId(
                householdId,
                storageZoneId
        );
    }

    public List<PantryItem> findByCategory(
            UUID householdId,
            UUID categoryId
    ) {
        return repository.findByHouseholdIdAndCategoryId(
                householdId,
                categoryId
        );
    }

    public List<PantryItem> findByBarcode(String barcode) {
        return repository.findByBarcode(barcode);
    }

    public PantryItem update(PantryItem item) {
        return repository.save(item);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Pantry item not found");
        }

        repository.deleteById(id);
    }

    public PantryItem consume(UUID id) {
        PantryItem item = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pantry item not found"));

        item.setIsConsumed(true);

        return repository.save(item);
    }

    public PantryItem toggleFavorite(UUID id) {
        PantryItem item = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pantry item not found"));

        item.setIsFavorite(!item.getIsFavorite());

        return repository.save(item);
    }
}