package com.storemini.payload.request;

import com.storemini.model.user.enums.PaymentMethod;
import lombok.Data;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String name;
    private String phone;
    private String address;
    private PaymentMethod paymentMethod;
    private List<OrderItemRequest> items;
}