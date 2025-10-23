package com.storemini.model.user.enums;

public enum OrderStatus {
    PENDING,     // Chờ xác nhận
    CONFIRMED,   // Đã xác nhận
    SHIPPING,    // Đang giao hàng
    COMPLETED,   // Đã giao thành công
    CANCELED     // Đã hủy
}
