package com.storemini.controller.user.user;

import com.storemini.model.user.entity.OrderEntity;
import com.storemini.model.user.repository.OrderRepository;
import com.storemini.payload.request.OrderRequest;
import com.storemini.payload.request.OrderStatusRequest;
import com.storemini.service.user.OrderService;
import com.storemini.service.user.VnpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final VnpayService vnpayService;
    private final OrderRepository orderRepository;


    @PostMapping
    public ResponseEntity<OrderEntity> createOrder(@RequestBody OrderRequest request) {
        OrderEntity order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderEntity>> getOrdersByPhone(@RequestParam String phone) {
        List<OrderEntity> orders = orderService.getOrdersByPhone(phone);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderEntity> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/payment")
    public ResponseEntity<?> createOrderVnpay(@RequestBody OrderRequest request) {
        // Tạo đơn hàng ở trạng thái "PENDING"
        OrderEntity order = orderService.createOrder(request);
        // Tạo link thanh toán VNPAY
        String paymentUrl = vnpayService.createPaymentUrl(order.getId(), order.getTotalPrice().longValue());
        // Trả về URL cho frontend redirect
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    @PostMapping("/updateStatus")
    public ResponseEntity<?> updatePaymentStatus(@RequestBody OrderStatusRequest request) {
        OrderEntity updatedOrder = orderService.updateOrderStatus(request);
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái đơn hàng thành công",
                "status", updatedOrder.getStatus()
        ));
    }



}
