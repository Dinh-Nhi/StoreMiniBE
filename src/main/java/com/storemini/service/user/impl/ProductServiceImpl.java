package com.storemini.service.user.impl;

import com.storemini.model.user.entity.ProductEntity;
import com.storemini.model.user.entity.ProductSizeEntity;
import com.storemini.model.user.entity.ProductVariantEntity;
import com.storemini.model.user.repository.*;
import com.storemini.service.user.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductSizeRepository sizeRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    // ===============================
    // CRUD PRODUCT
    // ===============================

    @Override
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<ProductEntity> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public ProductEntity createProduct(ProductEntity product) {
        // Kiểm tra Category & Brand
        if (product.getCategoryId() != null &&
                !categoryRepository.existsById(product.getCategoryId())) {
            throw new RuntimeException("Category ID không tồn tại: " + product.getCategoryId());
        }

        if (product.getBrandId() != null &&
                !brandRepository.existsById(product.getBrandId())) {
            throw new RuntimeException("Brand ID không tồn tại: " + product.getBrandId());
        }

        // Lưu product
        ProductEntity saved = productRepository.save(product);

        // Lưu các variant
        if (product.getVariants() != null) {
            for (ProductVariantEntity variant : product.getVariants()) {
                variant.setProduct(saved);
                ProductVariantEntity savedVariant = variantRepository.save(variant);

                // Lưu sizes
                if (variant.getSizes() != null) {
                    for (ProductSizeEntity size : variant.getSizes()) {
                        size.setVariant(savedVariant);
                        sizeRepository.save(size);
                    }
                }
            }
        }

        return saved;
    }

    @Override
    public ProductEntity updateProduct(Long id, ProductEntity updatedProduct) {
        return productRepository.findById(id).map(existing -> {

            // 🔹 Cập nhật thông tin cơ bản
            if (updatedProduct.getName() != null)
                existing.setName(updatedProduct.getName());
            if (updatedProduct.getDescription() != null)
                existing.setDescription(updatedProduct.getDescription());
            if (updatedProduct.getBasePrice() != null)
                existing.setBasePrice(updatedProduct.getBasePrice());
            if (updatedProduct.getActive() != null)
                existing.setActive(updatedProduct.getActive());

            // 🔹 Kiểm tra Category
            if (updatedProduct.getCategoryId() != null) {
                boolean exists = categoryRepository.existsById(updatedProduct.getCategoryId());
                if (!exists) {
                    throw new RuntimeException("Không tìm thấy Category ID: " + updatedProduct.getCategoryId());
                }
                existing.setCategoryId(updatedProduct.getCategoryId());
            }

            // 🔹 Kiểm tra Brand
            if (updatedProduct.getBrandId() != null) {
                boolean exists = brandRepository.existsById(updatedProduct.getBrandId());
                if (!exists) {
                    throw new RuntimeException("Không tìm thấy Brand ID: " + updatedProduct.getBrandId());
                }
                existing.setBrandId(updatedProduct.getBrandId());
            }

            // 🔹 Lưu lại sản phẩm
            ProductEntity saved = productRepository.save(existing);

            // 🔹 Cập nhật danh sách variants (nếu có truyền vào)
            if (updatedProduct.getVariants() != null) {
                // Xóa variants cũ trước khi thêm mới
                variantRepository.deleteAllByProductId(saved.getId());

                for (ProductVariantEntity variant : updatedProduct.getVariants()) {
                    variant.setProduct(saved); // gán lại quan hệ
                    ProductVariantEntity savedVariant = variantRepository.save(variant);

                    // Nếu có sizes thì lưu kèm
                    if (variant.getSizes() != null) {
                        for (ProductSizeEntity size : variant.getSizes()) {
                            size.setVariant(savedVariant);
                            sizeRepository.save(size);
                        }
                    }
                }
            }

            return saved;
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + id));
    }



    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sản phẩm ID: " + id);
        }
        productRepository.deleteById(id);
    }

    // ===============================
    // CRUD VARIANT / SIZE / IMAGE
    // ===============================

    @Override
    public ProductVariantEntity addVariant(Long productId, ProductVariantEntity variant) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId));
        variant.setProduct(product);
        return variantRepository.save(variant);
    }

    @Override
    public ProductSizeEntity addSize(Long variantId, ProductSizeEntity size) {
        ProductVariantEntity variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy variant ID: " + variantId));
        size.setVariant(variant);
        return sizeRepository.save(size);
    }
}