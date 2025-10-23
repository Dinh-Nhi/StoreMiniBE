package com.storemini.service.user;

import com.storemini.model.user.entity.*;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    // ===============================
    // CRUD PRODUCT
    // ===============================
    List<ProductEntity> getAllProducts();

    Optional<ProductEntity> getProductById(Long id);

    ProductEntity createProduct(ProductEntity product);

    ProductEntity updateProduct(Long id, ProductEntity updatedProduct);

    void deleteProduct(Long id);

    List<ProductEntity> getProductsByCategory(Long categoryId);
    // ===============================
    // CRUD VARIANT / SIZE
    // ===============================
    ProductVariantEntity addVariant(Long productId, ProductVariantEntity variant);
    ProductSizeEntity addSize(Long variantId, ProductSizeEntity size);
    List<ProductEntity> getBestSellingProducts(int limit);
    List<ProductEntity> getDiscountedProducts(int limit);
}