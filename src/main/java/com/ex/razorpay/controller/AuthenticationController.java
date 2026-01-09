package com.ex.razorpay.controller;

import com.ex.razorpay.dto.AdminRegisterRequest;
import com.ex.razorpay.dto.AuthenticationRequest;
import com.ex.razorpay.dto.AuthenticationResponse;
import com.ex.razorpay.dto.RegisterRequest;
import com.ex.razorpay.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request  ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
//    @PostMapping("/login")
//    public ResponseEntity<AuthenticationResponse> authenticate(
//            @RequestBody AuthenticationRequest request
//    ) {
//        return ResponseEntity.ok(authenticationService.authenticate(request));
//    }
    
//    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponse> register(
//            @RequestBody RegisterRequest request
//    ) {
//        return ResponseEntity.ok(authenticationService.register(request));
//    }
//    
    @PostMapping("/register-admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(
            @RequestBody AdminRegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.registerAdmin(request));
    }
    
 
    
    // Admin login (same as user login but with role check)
    @PostMapping("/admin-login")
    public ResponseEntity<AuthenticationResponse> adminLogin(
            @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        
        // Check if the logged in user has ADMIN role
        if (response.isSuccess() && !response.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.ok(
                AuthenticationResponse.builder()
                    .success(false)
                    .message("Access denied. Admin privileges required.")
                    .build()
            );
        }
        
        return ResponseEntity.ok(response);
    }
}