package com.storemini.controller.user.user;

import com.storemini.model.user.entity.CategoryEntity;
import com.storemini.model.user.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/categories")
@RequiredArgsConstructor

public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryEntity>> getAll() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryEntity> getById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryEntity> create(@RequestBody CategoryEntity category) {
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryEntity> update(@PathVariable Long id, @RequestBody CategoryEntity update) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(update.getName());
                    existing.setDescription(update.getDescription());
                    return ResponseEntity.ok(categoryRepository.save(existing));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getByIsShow")
    public ResponseEntity<List<CategoryEntity>> getByIsShow() {
        return ResponseEntity.ok(categoryRepository.findIsShowCategories());
    }
}
