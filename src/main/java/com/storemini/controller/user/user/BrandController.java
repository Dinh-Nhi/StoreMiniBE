package com.storemini.controller.user.user;

import com.storemini.model.user.entity.BrandEntity;
import com.storemini.model.user.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/brands")
@RequiredArgsConstructor

public class BrandController {

    private final BrandRepository brandRepository;

    @GetMapping
    public ResponseEntity<List<BrandEntity>> getAll() {
        return ResponseEntity.ok(brandRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandEntity> getById(@PathVariable Long id) {
        return brandRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BrandEntity> create(@RequestBody BrandEntity brand) {
        return ResponseEntity.ok(brandRepository.save(brand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandEntity> update(@PathVariable Long id, @RequestBody BrandEntity update) {
        return brandRepository.findById(id)
                .map(existing -> {
                    existing.setName(update.getName());
                    existing.setCountry(update.getCountry());
                    return ResponseEntity.ok(brandRepository.save(existing));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}