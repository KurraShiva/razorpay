package com.ex.razorpay.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderDTO {
    private Long userId;
    private List<OrderItemDTO> items;
    private String currency;
}