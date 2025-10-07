package com.storemini.model.user.repository;

import com.storemini.model.user.entity.ProductEntity;
import com.storemini.model.user.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, Long> {

    // Lấy danh sách variant theo sản phẩm
    List<ProductVariantEntity> findByProduct(ProductEntity product);

    // Lấy variant theo màu
    List<ProductVariantEntity> findByColorIgnoreCase(String color);

    // Lấy variant theo sản phẩm và màu
    ProductVariantEntity findByProductAndColorIgnoreCase(ProductEntity product, String color);

    // Lấy tất cả variant còn hàng
    @Query("SELECT v FROM ProductVariantEntity v WHERE v.stock > 0 AND v.available = true")
    List<ProductVariantEntity> findAvailableVariants();

//    // Lấy variant và load danh sách size, ảnh
//    @Query("SELECT DISTINCT v FROM ProductVariantEntity v " +
//            "LEFT JOIN FETCH v.sizes " +
//            "LEFT JOIN FETCH v.images " +
//            "WHERE v.id = :id")
//    ProductVariantEntity findVariantWithDetails(@Param("id") Long id);

    void deleteAllByProductId(Long productId);
}