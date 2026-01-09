package com.ex.razorpay.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long userId;
    private List<OrderItemDTO> items;
    private String currency;
}