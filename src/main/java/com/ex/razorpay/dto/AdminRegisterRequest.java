package com.ex.razorpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRegisterRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String adminSecret; // Secret key for admin registration
}