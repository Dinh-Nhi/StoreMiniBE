package com.storemini.controller.admin.product;

import com.storemini.model.user.entity.ProductEntity;
import com.storemini.service.admin.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService productService;

    @GetMapping("/getAll")
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductEntity product) {
        ProductEntity created = productService.createProduct(product);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductEntity> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductEntity updatedProduct
    ) {
        ProductEntity updated = productService.updateProduct(id, updatedProduct);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/process")
    public ResponseEntity<ProductEntity> processProduct(@RequestBody ProductEntity product) {
        if (product.getId() == null) {
            ProductEntity created = productService.createProduct(product);
            return ResponseEntity.ok(created);
        } else {
            ProductEntity updated = productService.updateProduct(product.getId(), product);
            return ResponseEntity.ok(updated);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
