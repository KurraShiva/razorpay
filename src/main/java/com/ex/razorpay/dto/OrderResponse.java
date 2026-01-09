package com.ex.razorpay.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponse {

	private String orderId;
    private String razorpayOrderId;
    private BigDecimal amount;
    private String currency;
    private String status;
}
