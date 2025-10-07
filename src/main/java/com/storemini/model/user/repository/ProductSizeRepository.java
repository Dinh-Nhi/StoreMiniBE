package com.storemini.model.user.repository;

import com.storemini.model.user.entity.ProductSizeEntity;
import com.storemini.model.user.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSizeEntity, Long> {

    // Lấy danh sách size theo variant
    List<ProductSizeEntity> findByVariant(ProductVariantEntity variant);

    // Lấy size cụ thể theo tên
    ProductSizeEntity findByVariantAndSize(ProductVariantEntity variant, String size);
}
