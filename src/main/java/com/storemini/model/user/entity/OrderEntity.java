package com.storemini.model.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.storemini.model.user.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String status;

    private BigDecimal totalPrice;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"order"}) // tránh lặp khi serialize
    private List<OrderItemEntity> items;
}
