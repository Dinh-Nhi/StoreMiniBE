package com.storemini.service.user;

import com.storemini.model.user.entity.OrderEntity;
import com.storemini.payload.request.OrderRequest;
import com.storemini.payload.request.OrderStatusRequest;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderEntity createOrder(OrderRequest request);
    List<OrderEntity> getOrdersByName(String phone);
    Optional<OrderEntity> getOrderById(Long id);
    OrderEntity updateOrderStatus(OrderStatusRequest request);
}