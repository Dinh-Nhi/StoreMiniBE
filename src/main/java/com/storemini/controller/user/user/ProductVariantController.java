package com.storemini.controller.user.user;

import com.storemini.model.user.entity.ProductVariantEntity;
import com.storemini.model.user.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantRepository variantRepository;

    @GetMapping
    public ResponseEntity<List<ProductVariantEntity>> getAll() {
        return ResponseEntity.ok(variantRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantEntity> getById(@PathVariable Long id) {
        return variantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductVariantEntity> create(@RequestBody ProductVariantEntity variant) {
        return ResponseEntity.ok(variantRepository.save(variant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductVariantEntity> update(@PathVariable Long id, @RequestBody ProductVariantEntity update) {
        return variantRepository.findById(id)
                .map(existing -> {
                    existing.setColor(update.getColor());
                    existing.setMaterial(update.getMaterial());
                    existing.setPrice(update.getPrice());
                    existing.setStock(update.getStock());
                    return ResponseEntity.ok(variantRepository.save(existing));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        variantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}