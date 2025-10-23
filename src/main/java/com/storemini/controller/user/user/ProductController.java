package com.storemini.controller.user.user;

import com.storemini.model.user.entity.ProductEntity;
import com.storemini.model.user.entity.ProductSizeEntity;
import com.storemini.model.user.entity.ProductVariantEntity;
import com.storemini.service.user.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductEntity product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductEntity> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductEntity updatedProduct) {
        return ResponseEntity.ok(productService.updateProduct(id, updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getProductByCategoryId(@PathVariable Long id) {
        List<ProductEntity> products = productService.getProductsByCategory(id);

        if (products == null || products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/best-selling")
    public ResponseEntity<List<ProductEntity>> getBestSellingProducts(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<ProductEntity> products = productService.getBestSellingProducts(limit);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/discounted")
    public List<ProductEntity> getDiscountedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return productService.getDiscountedProducts(limit);
    }
}