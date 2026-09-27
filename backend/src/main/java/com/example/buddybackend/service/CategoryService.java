package com.example.buddybackend.service;

import com.example.buddybackend.entity.Category;
import com.example.buddybackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category create(Category category) {
        return repository.save(category);
    }

    public Optional<Category> findById(UUID id) {
        return repository.findById(id);
    }

    public List<Category> findByHouseholdId(UUID householdId) {
        return repository.findByHouseholdIdOrHouseholdIdIsNull(
                householdId
        );
    }

    public List<Category> findGlobalCategories() {
        return repository.findByHouseholdIdIsNull();
    }

    public Category update(Category category) {
        return repository.save(category);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}