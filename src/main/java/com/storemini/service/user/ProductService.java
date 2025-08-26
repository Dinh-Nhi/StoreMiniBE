package com.storemini.service.user;

import com.storemini.model.entity.user.Product;

import java.util.Optional;

public interface ProductService {
    Optional<Product> findById(String id);
}
