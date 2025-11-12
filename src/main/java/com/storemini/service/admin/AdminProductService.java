package com.storemini.service.admin;

import com.storemini.model.user.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface AdminProductService {
    List<ProductEntity> getAllProducts();

    Optional<ProductEntity> getProductById(Long id);

    ProductEntity createProduct(ProductEntity product);

    ProductEntity updateProduct(Long id, ProductEntity updatedProduct);

    void deleteProduct(Long id);
}

