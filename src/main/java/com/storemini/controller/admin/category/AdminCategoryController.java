package com.storemini.controller.admin.category;

import com.storemini.model.user.entity.CategoryEntity;
import com.storemini.model.user.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping("/getAll")
    public ResponseEntity<List<CategoryEntity>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<CategoryEntity> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.badRequest().body("Category with ID " + id + " not found!");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<CategoryEntity> saveOrUpdateCategory(@RequestBody CategoryEntity categoryEntity) {
        if (categoryEntity.getId() != null) {
            Optional<CategoryEntity> existing = categoryRepository.findById(categoryEntity.getId());
            if (existing.isPresent()) {
                CategoryEntity update = existing.get();
                update.setName(categoryEntity.getName());
                update.setDescription(categoryEntity.getDescription());
                update.setIsShow(categoryEntity.getIsShow());
                return ResponseEntity.ok(categoryRepository.save(update));
            }
        }
        return ResponseEntity.ok(categoryRepository.save(categoryEntity));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        Optional<CategoryEntity> existing = categoryRepository.findById(id);
        if (existing.isPresent()) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("Deleted successfully with ID: " + id);
        } else {
            return ResponseEntity.badRequest().body("Category with ID " + id + " not found!");
        }
    }
}
