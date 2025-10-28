package com.storemini.payload.request;

import com.storemini.model.user.enums.PaymentMethod;
import lombok.Data;

import java.util.List;

@Data
public class OrderStatusRequest {
    private Long orderId;
}