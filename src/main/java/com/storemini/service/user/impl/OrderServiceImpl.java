package com.storemini.service.user.impl;

import com.storemini.model.user.entity.OrderEntity;
import com.storemini.model.user.entity.OrderItemEntity;
import com.storemini.model.user.entity.ProductSizeEntity;
import com.storemini.model.user.entity.ProductVariantEntity;
import com.storemini.model.user.repository.OrderRepository;
import com.storemini.model.user.repository.ProductVariantRepository;
import com.storemini.payload.request.OrderItemRequest;
import com.storemini.payload.request.OrderRequest;
import com.storemini.payload.request.OrderStatusRequest;
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
    @Transactional
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
            // 🔹 Lấy variant theo ID
            ProductVariantEntity variant = variantRepository.findById(itemReq.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found: " + itemReq.getVariantId()));

            // 🔹 Kiểm tra đúng màu (mỗi variant ứng với 1 màu)
            if (!variant.getColor().equalsIgnoreCase(itemReq.getColor())) {
                throw new RuntimeException(
                        "Color mismatch for variant ID " + variant.getId() +
                                ": expected " + variant.getColor() + ", got " + itemReq.getColor()
                );
            }

            // 🔹 Lấy đúng size theo tên (vì size là String, ví dụ: "M", "L", "XL")
            ProductSizeEntity sizeEntity = variant.getSizes().stream()
                    .filter(s -> s.getSize().equalsIgnoreCase(itemReq.getSize()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Size '" + itemReq.getSize() + "' not found for variant " + variant.getId()
                    ));

            // 🔹 Trừ tồn kho size
            int newSizeStock = sizeEntity.getStock() - itemReq.getQuantity();
            if (newSizeStock < 0)
                throw new RuntimeException("Hết hàng cho size " + sizeEntity.getSize() + " (" + variant.getColor() + ")");
            sizeEntity.setStock(newSizeStock);

            // 🔹 Trừ tồn kho tổng của variant (theo màu)
            int newVariantStock = variant.getStock() - itemReq.getQuantity();
            if (newVariantStock < 0)
                throw new RuntimeException("Hết hàng cho màu " + variant.getColor());
            variant.setStock(newVariantStock);

            // 🔹 Cập nhật sold
            variant.setSold(variant.getSold() + itemReq.getQuantity());

            // 🔹 Tạo chi tiết đơn hàng
            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setVariant(variant);
            item.setProductName(variant.getProduct().getName());
            item.setColor(variant.getColor());
            item.setSize(sizeEntity.getSize()); // ✅ size là String
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(variant.getPrice());
            items.add(item);

            // 🔹 Cộng tổng tiền
            BigDecimal lineTotal = variant.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(lineTotal);
        }

        // 🔹 Gán danh sách item + tổng giá
        order.setItems(items);
        order.setTotalPrice(total);

        // 🔹 Lưu order và cập nhật variant/size
        OrderEntity savedOrder = orderRepository.save(order);
        variantRepository.saveAll(
                items.stream().map(OrderItemEntity::getVariant).distinct().toList()
        );

        return savedOrder;
    }

    @Override
    public List<OrderEntity> getOrdersByName(String name) {
        return orderRepository.findByCustomerName(name);
    }

    @Override
    public Optional<OrderEntity> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional
    public OrderEntity updateOrderStatus(OrderStatusRequest request) {
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        //Cập nhật sold cho các sản phẩm
        List<ProductVariantEntity> entityList = new ArrayList<>();
        order.getItems().forEach(item -> {
            ProductVariantEntity variant = item.getVariant();
            // Cập nhật số lượng đã bán
            int newSold = (variant.getSold() == null ? 0 : variant.getSold()) + item.getQuantity();
            variant.setSold(newSold);
            entityList.add(variant);
        });
        variantRepository.saveAll(entityList);
        //Cập nhật trạng thái đơn hàng
        order.setStatus("PAYMENT");
        return orderRepository.save(order);
    }

}
