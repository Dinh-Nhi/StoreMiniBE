package com.storemini.payload.request;


import lombok.Data;

@Data
public class OrderItemRequest {
    private Long variantId;
    private Integer quantity;
    private String size;
    private String color;
}