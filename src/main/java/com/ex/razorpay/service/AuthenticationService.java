//package com.ex.razorpay.service;
//
//import com.ex.razorpay.dto.AuthenticationRequest;
//import com.ex.razorpay.dto.AuthenticationResponse;
//import com.ex.razorpay.dto.RegisterRequest;
//import com.ex.razorpay.entity.User;
//import com.ex.razorpay.enumm.Role;
//import com.ex.razorpay.repository.UserRepository;
//import com.ex.razorpay.security.JwtService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthenticationService {
//    
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//    
//    public AuthenticationResponse register(RegisterRequest request) {
//        // Check if user already exists
//        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//            return AuthenticationResponse.builder()
//                    .message("User already exists with this email")
//                    .build();
//        }
//        
//        var user = User.builder()
//                .name(request.getName())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .phone(request.getPhone())
//                .address(request.getAddress())
//                .role(Role.ROLE_USER) // Default role for new users
//                .build();
//        
//        var savedUser = userRepository.save(user);
//        var jwtToken = jwtService.generateToken(user.getEmail());
//        
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .email(savedUser.getEmail())
//                .role(savedUser.getRole().name())
//                .userId(savedUser.getId())
//                .message("User registered successfully")
//                .build();
//    }
//    
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//        
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        
//        String jwtToken = jwtService.generateToken(user.getEmail());
//        
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .email(user.getEmail())
//                .role(user.getRole().name())
//                .userId(user.getId())
//                .message("Login successful")
//                .build();
//    }
//}


package com.ex.razorpay.service;

import com.ex.razorpay.dto.*;
import com.ex.razorpay.entity.User;
import com.ex.razorpay.enumm.Role;
import com.ex.razorpay.repository.UserRepository;
import com.ex.razorpay.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Value("${admin.secret.key}")
    private String adminSecretKey;
    
    @Value("${admin.default.email}")
    private String defaultAdminEmail;
    
    @Value("${admin.default.password}")
    private String defaultAdminPassword;
    
    // User registration
    public AuthenticationResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return AuthenticationResponse.builder()
                    .success(false)
                    .message("User already exists with this email")
                    .build();
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(Role.ROLE_USER)
                .build();
        
        User savedUser = userRepository.save(user);
//        String jwtToken = jwtService.generateToken(user.getUsername());
        
        return AuthenticationResponse.builder()
                .success(true)
//                .token(jwtToken)
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .userId(savedUser.getId())
                .message("User registered successfully")
                .build();
    }
    
    // Admin registration with secret key
    public AuthenticationResponse registerAdmin(AdminRegisterRequest request) {
        // Check admin secret key
        if (!adminSecretKey.equals(request.getAdminSecret())) {
            return AuthenticationResponse.builder()
                    .success(false)
                    .message("Invalid admin secret key")
                    .build();
        }
        
        // Check if admin already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return AuthenticationResponse.builder()
                    .success(false)
                    .message("Admin already exists with this email")
                    .build();
        }
        
        User admin = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(Role.ROLE_ADMIN)
                .build();
        
        User savedAdmin = userRepository.save(admin);
        String jwtToken = jwtService.generateToken(admin.getUsername());
        
        return AuthenticationResponse.builder()
                .success(true)
                .token(jwtToken)
                .email(savedAdmin.getEmail())
                .role(savedAdmin.getRole().name())
                .userId(savedAdmin.getId())
                .message("Admin registered successfully")
                .build();
    }
    
    // Login for both users and admins
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String jwtToken = jwtService.generateToken(user.getUsername());
            
            return AuthenticationResponse.builder()
                    .success(true)
                    .token(jwtToken)
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .userId(user.getId())
                    .message("Login successful")
                    .build();
                    
        } catch (Exception e) {
            return AuthenticationResponse.builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
        }
    }
    
    // Create default admin if not exists (for first time setup)
    public void createDefaultAdminIfNotExists() {
        if (userRepository.findByEmail(defaultAdminEmail).isEmpty()) {
            var admin = User.builder()
                    .name("Super Admin")
                    .email(defaultAdminEmail)
                    .password(passwordEncoder.encode(defaultAdminPassword))
                    .phone("9999999999")
                    .address("System Admin")
                    .role(Role.ROLE_ADMIN)
                    .build();
            
            userRepository.save(admin);
            System.out.println("Default admin created: " + defaultAdminEmail);
        }
    }
}