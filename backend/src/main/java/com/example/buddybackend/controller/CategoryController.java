package com.example.buddybackend.controller;

import com.example.buddybackend.dto.CategoryRequest;
import com.example.buddybackend.dto.CategoryResponse;
import com.example.buddybackend.entity.Category;
import com.example.buddybackend.service.CategoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping("/household/{householdId}")
    public ResponseEntity<CategoryResponse> create(
            @PathVariable UUID householdId,
            @RequestBody CategoryRequest request
    ) {
        Category category = new Category();

        category.setHouseholdId(householdId);
        category.setName(request.getName());
        category.setColorHex(request.getColorHex());
        category.setIcon(request.getIcon());

        return ResponseEntity.ok(
                toResponse(service.create(category))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @PathVariable UUID id
    ) {
        return service.findById(id)
                .map(category ->
                        ResponseEntity.ok(toResponse(category)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<CategoryResponse>> getAll(
            @PathVariable UUID householdId
    ) {
        return ResponseEntity.ok(
                service.findByHouseholdId(householdId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/global")
    public ResponseEntity<List<CategoryResponse>> getGlobal() {
        return ResponseEntity.ok(
                service.findGlobalCategories()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @RequestBody CategoryRequest request
    ) {
        Category category = service.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        category.setName(request.getName());
        category.setColorHex(request.getColorHex());
        category.setIcon(request.getIcon());

        return ResponseEntity.ok(
                toResponse(service.update(category))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();

        response.setId(category.getId());
        response.setHouseholdId(category.getHouseholdId());
        response.setName(category.getName());
        response.setColorHex(category.getColorHex());
        response.setIcon(category.getIcon());
        response.setCreatedAt(category.getCreatedAt());

        return response;
    }
}