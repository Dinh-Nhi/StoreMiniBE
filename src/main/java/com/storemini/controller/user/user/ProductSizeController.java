package com.storemini.controller.user.user;

import com.storemini.model.user.entity.ProductSizeEntity;
import com.storemini.model.user.repository.ProductSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/sizes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductSizeController {

    private final ProductSizeRepository sizeRepository;

    @GetMapping
    public ResponseEntity<List<ProductSizeEntity>> getAll() {
        return ResponseEntity.ok(sizeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductSizeEntity> getById(@PathVariable Long id) {
        return sizeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductSizeEntity> create(@RequestBody ProductSizeEntity size) {
        return ResponseEntity.ok(sizeRepository.save(size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductSizeEntity> update(@PathVariable Long id, @RequestBody ProductSizeEntity update) {
        return sizeRepository.findById(id)
                .map(existing -> {
                    existing.setSize(update.getSize());
                    existing.setStock(update.getStock());
                    return ResponseEntity.ok(sizeRepository.save(existing));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sizeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
