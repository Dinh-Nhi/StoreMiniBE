package com.storemini.service.user.impl;

import com.storemini.model.user.entity.OrderEntity;
import com.storemini.model.user.entity.OrderItemEntity;
import com.storemini.model.user.entity.ProductVariantEntity;
import com.storemini.model.user.repository.OrderRepository;
import com.storemini.model.user.repository.ProductVariantRepository;
import com.storemini.payload.request.OrderItemRequest;
import com.storemini.payload.request.OrderRequest;
import com.storemini.service.user.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    public OrderEntity createOrder(OrderRequest request) {
        OrderEntity order = new OrderEntity();
        order.setCustomerName(request.getName());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("PENDING");

        List<OrderItemEntity> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            ProductVariantEntity variant = variantRepository.findById(itemReq.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found: " + itemReq.getVariantId()));

            // Tạo chi tiết đơn hàng
            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setVariant(variant);
            item.setProductName(variant.getProduct().getName());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(variant.getPrice());

            items.add(item);

            // Tính tổng
            BigDecimal lineTotal = variant.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(lineTotal);

            // Giảm tồn kho
            int newStock = variant.getStock() - itemReq.getQuantity();
            if (newStock < 0) throw new RuntimeException("Hết hàng cho variant ID " + variant.getId());
            variant.setStock(newStock);
        }

        order.setItems(items);
        order.setTotalPrice(total);

        return orderRepository.save(order);
    }

    @Override
    public List<OrderEntity> getOrdersByPhone(String phone) {
        return orderRepository.findByPhone(phone);
    }

    @Override
    public Optional<OrderEntity> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
