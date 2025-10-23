package com.storemini.service.user;

import com.storemini.model.user.entity.OrderEntity;
import com.storemini.payload.request.OrderRequest;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderEntity createOrder(OrderRequest request);
    List<OrderEntity> getOrdersByPhone(String phone);
    Optional<OrderEntity> getOrderById(Long id);
}