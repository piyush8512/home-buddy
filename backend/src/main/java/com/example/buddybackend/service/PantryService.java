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
public class PantryService {

    private final PantryItemRepository pantryItemRepository;

    public PantryService(PantryItemRepository pantryItemRepository) {
        this.pantryItemRepository = pantryItemRepository;
    }

    public List<PantryItem> findByUserId(UUID userId) {
        return pantryItemRepository.findByUserId(userId);
    }

    public Optional<PantryItem> findById(UUID id) {
        return pantryItemRepository.findById(id);
    }

    public PantryItem create(PantryItem item) {
        return pantryItemRepository.save(item);
    }

    public PantryItem update(PantryItem item) {
        return pantryItemRepository.save(item);
    }

    public void delete(UUID id) {
        pantryItemRepository.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return pantryItemRepository.existsById(id);
    }
}