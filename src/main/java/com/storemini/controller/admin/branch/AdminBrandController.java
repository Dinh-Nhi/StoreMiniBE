package com.storemini.controller.admin.branch;

import com.storemini.model.user.entity.BrandEntity;
import com.storemini.model.user.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/branch")
@RequiredArgsConstructor
public class AdminBrandController {

    private final BrandRepository brandRepository;

    @GetMapping("/getAll")
    public ResponseEntity<List<BrandEntity>> getAllBrands() {
        return ResponseEntity.ok(brandRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable Long id) {
        Optional<BrandEntity> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            return ResponseEntity.ok(brand.get());
        } else {
            return ResponseEntity.badRequest().body("Brand with ID " + id + " not found!");
        }
    }

    @PostMapping("/process")
    public ResponseEntity<BrandEntity> saveOrUpdateBrand(@RequestBody BrandEntity brandEntity) {
        if (brandEntity.getId() != null) {
            Optional<BrandEntity> existing = brandRepository.findById(brandEntity.getId());
            if (existing.isPresent()) {
                BrandEntity update = existing.get();
                update.setName(brandEntity.getName());
                update.setCountry(brandEntity.getCountry());
                return ResponseEntity.ok(brandRepository.save(update));
            }
        }
        return ResponseEntity.ok(brandRepository.save(brandEntity));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id) {
        Optional<BrandEntity> existing = brandRepository.findById(id);
        if (existing.isPresent()) {
            brandRepository.deleteById(id);
            return ResponseEntity.ok("Deleted successfully with ID: " + id);
        } else {
            return ResponseEntity.badRequest().body("Brand with ID " + id + " not found!");
        }
    }
}
