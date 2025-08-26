package com.storemini.service.impl.user;

import com.storemini.model.entity.user.Product;
import com.storemini.model.repository.user.ProductRepository;
import com.storemini.service.user.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductImpl implements ProductService {
    private final ProductRepository repository;

    @Override
    public Optional<Product> findById(String id) {
        return repository.findById(id);
    }
}
