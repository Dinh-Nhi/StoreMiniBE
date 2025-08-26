package com.storemini.controller.user.product;

import com.storemini.model.entity.user.Product;
import com.storemini.service.user.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/product")
public class ProductController {

    private final ProductService productService;


    @GetMapping("/detail/{id}")
    public ResponseEntity<Product> detailProduct(@PathVariable String id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
