package com.ex.razorpay.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    
    private final AuthenticationService authenticationService;
    
    @PostConstruct
    public void init() {
        // Create default admin user on application start
        authenticationService.createDefaultAdminIfNotExists();
    }
}