package com.storemini.controller.admin.order;

import com.storemini.model.user.entity.OrderEntity;
import com.storemini.model.user.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;

    @GetMapping("/getAll")
    public ResponseEntity<List<OrderEntity>> getAllCategories() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<OrderEntity> category = orderRepository.findById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.badRequest().body("Category with ID " + id + " not found!");
        }
    }

    @PostMapping("/process")
    public ResponseEntity<?> processOrder(@RequestBody Map<String, Object> request) {
        // Lấy dữ liệu từ body
        Long id = ((Number) request.get("id")).longValue();
        String status = (String) request.get("status");

        // Tìm đơn hàng trong DB
        Optional<OrderEntity> existing = orderRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy đơn hàng có ID: " + id);
        }

        // Cập nhật trạng thái
        OrderEntity order = existing.get();
        order.setStatus(status);

        // Lưu lại vào DB
        OrderEntity updatedOrder = orderRepository.save(order);

        return ResponseEntity.ok(updatedOrder);
    }

}
