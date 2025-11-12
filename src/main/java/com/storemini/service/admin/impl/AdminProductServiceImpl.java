package com.storemini.service.admin.impl;

import com.storemini.model.user.entity.*;
import com.storemini.model.user.repository.*;
import com.storemini.service.admin.AdminProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductSizeRepository sizeRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    // ----------------------------
    // LẤY DỮ LIỆU
    // ----------------------------
    @Override
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<ProductEntity> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // ----------------------------
    // TẠO MỚI SẢN PHẨM
    // ----------------------------
    @Override
    public ProductEntity createProduct(ProductEntity product) {
        validateRelations(product);

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // Gán quan hệ product -> variant -> size
        if (product.getVariants() != null) {
            for (ProductVariantEntity variant : product.getVariants()) {
                variant.setProduct(product);
                if (variant.getSizes() != null) {
                    for (ProductSizeEntity size : variant.getSizes()) {
                        size.setVariant(variant);
                    }
                }
            }
        }

        return productRepository.save(product); // Cascade.ALL sẽ tự lưu variants + sizes
    }

    // ----------------------------
    // CẬP NHẬT SẢN PHẨM
    // ----------------------------
    @Override
    public ProductEntity updateProduct(Long id, ProductEntity updatedProduct) {
        return productRepository.findById(id).map(existing -> {

            // 🔹 Cập nhật thông tin cơ bản
            existing.setName(updatedProduct.getName());
            existing.setDescription(updatedProduct.getDescription());
            existing.setBasePrice(updatedProduct.getBasePrice());
            existing.setDiscount(updatedProduct.getDiscount());
            existing.setFileKey(updatedProduct.getFileKey());
            existing.setActive(updatedProduct.getActive());
            existing.setIsNew(updatedProduct.getIsNew());
            existing.setIsShow(updatedProduct.getIsShow());
            existing.setUpdatedAt(LocalDateTime.now());

            // 🔹 Kiểm tra & cập nhật Brand / Category
            if (updatedProduct.getCategoryId() != null)
                validateCategory(updatedProduct.getCategoryId());
            if (updatedProduct.getBrandId() != null)
                validateBrand(updatedProduct.getBrandId());

            existing.setCategoryId(updatedProduct.getCategoryId());
            existing.setBrandId(updatedProduct.getBrandId());

            // 🔹 Cập nhật variants
            if (updatedProduct.getVariants() != null) {
                // Xóa hết variant cũ + size liên quan
                variantRepository.deleteAllByProductId(existing.getId());

                for (ProductVariantEntity variant : updatedProduct.getVariants()) {
                    variant.setProduct(existing);
                    ProductVariantEntity savedVariant = variantRepository.save(variant);

                    if (variant.getSizes() != null) {
                        for (ProductSizeEntity size : variant.getSizes()) {
                            size.setVariant(savedVariant);
                            sizeRepository.save(size);
                        }
                    }
                }
            }

            return productRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + id));
    }

    // ----------------------------
    // XOÁ SẢN PHẨM
    // ----------------------------
    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sản phẩm ID: " + id);
        }
        productRepository.deleteById(id);
    }

    // ----------------------------
    // HELPER METHODS
    // ----------------------------
    private void validateRelations(ProductEntity product) {
        if (product.getCategoryId() != null)
            validateCategory(product.getCategoryId());
        if (product.getBrandId() != null)
            validateBrand(product.getBrandId());
    }

    private void validateCategory(Long id) {
        if (!categoryRepository.existsById(id))
            throw new RuntimeException("Category ID không tồn tại: " + id);
    }

    private void validateBrand(Long id) {
        if (!brandRepository.existsById(id))
            throw new RuntimeException("Brand ID không tồn tại: " + id);
    }
}
