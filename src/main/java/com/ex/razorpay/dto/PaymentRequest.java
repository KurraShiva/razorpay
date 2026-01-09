package com.ex.razorpay.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private String paymentId;
    private String signature;
    private BigDecimal amount;
    private String currency;
}
