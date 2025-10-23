package com.storemini.model.user.repository;


import com.storemini.model.user.entity.BrandEntity;
import com.storemini.model.user.entity.CategoryEntity;
import com.storemini.model.user.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // Tìm sản phẩm theo tên gần đúng (cho trang tìm kiếm)
    List<ProductEntity> findByNameContainingIgnoreCase(String keyword);

    // Lấy danh sách sản phẩm theo danh mục
    List<ProductEntity> findByCategoryIdAndIsShowTrueAndActiveTrue(Long categoryId);

//    // Lấy danh sách sản phẩm theo thương hiệu
//    List<ProductEntity> findByBrand(BrandEntity brand);

    // Truy vấn nâng cao: sản phẩm đang hoạt động
    @Query("SELECT p FROM ProductEntity p WHERE p.active = true")
    List<ProductEntity> findActiveProducts();

    // Truy vấn có join: lấy sản phẩm và load các variant
    @Query("SELECT DISTINCT p FROM ProductEntity p LEFT JOIN FETCH p.variants WHERE p.id = :id")
    ProductEntity findProductWithVariants(@Param("id") Long id);


    @Query("""
        SELECT p FROM ProductEntity p
        JOIN p.variants v
        GROUP BY p
        ORDER BY SUM(v.Sold) DESC
    """)
    List<ProductEntity> findBestSellingProducts();
}