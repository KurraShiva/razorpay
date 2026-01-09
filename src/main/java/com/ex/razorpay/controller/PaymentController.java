////////
////////package com.ex.razorpay.controller;
////////
////////import com.ex.razorpay.dto.*;
////////import com.ex.razorpay.entity.Order;
////////import com.ex.razorpay.service.PaymentService;
////////import com.razorpay.RazorpayException;
////////import lombok.RequiredArgsConstructor;
////////import org.springframework.http.ResponseEntity;
////////import org.springframework.web.bind.annotation.*;
////////@RestController
////////@RequestMapping("/api/payments")
////////@RequiredArgsConstructor
////////public class PaymentController {
////////
////////    private final PaymentService paymentService;
////////
////////    @PostMapping("/create-order")
////////    public ResponseEntity<OrderResponse> createOrder(
////////            @RequestBody CreateOrderRequest request) throws RazorpayException {
////////        return ResponseEntity.ok(paymentService.createOrder(request));
////////    }
////////
////////    @PostMapping("/verify")
////////    public ResponseEntity<PaymentResponse> verifyPayment(
////////            @RequestBody PaymentRequest request) {
////////        return ResponseEntity.ok(paymentService.verifyPayment(request));
////////    }
////////
////////    @GetMapping("/order/{orderId}")
////////    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
////////        return ResponseEntity.ok(paymentService.getOrderByOrderId(orderId));
////////    }
////////}
//////
//////
//////package com.ex.razorpay.controller;
//////
//////import com.ex.razorpay.dto.*;
//////import com.ex.razorpay.service.PaymentService;
//////import com.razorpay.RazorpayException;
//////import lombok.RequiredArgsConstructor;
//////import org.springframework.http.ResponseEntity;
//////import org.springframework.web.bind.annotation.*;
//////
//////import java.math.BigDecimal;
//////
//////@RestController
//////@RequestMapping("/api/payments")
//////@RequiredArgsConstructor
//////public class PaymentController {
//////
//////    private final PaymentService paymentService;
//////
//////    @PostMapping("/create-order")
//////    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
//////        try {
//////            OrderResponse response = paymentService.createOrder(request);
//////            return ResponseEntity.ok(response);
//////        } catch (RazorpayException e) {
//////            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
//////        } catch (Exception e) {
//////            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//////        }
//////    }
//////
//////    @PostMapping("/verify")
//////    public ResponseEntity<?> verifyPayment(@RequestBody PaymentRequest request) {
//////        try {
//////            PaymentResponse response = paymentService.verifyPayment(request);
//////            return ResponseEntity.ok(response);
//////        } catch (Exception e) {
//////            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//////        }
//////    }
//////
//////    @GetMapping("/order/{orderId}")
//////    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {
//////        try {
//////            return ResponseEntity.ok(paymentService.getOrderByOrderId(orderId));
//////        } catch (Exception e) {
//////            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//////        }
//////    }
//////    
//////    @GetMapping("/razorpay-order/{razorpayOrderId}")
//////    public ResponseEntity<?> getRazorpayOrderDetails(@PathVariable String razorpayOrderId) {
//////        try {
//////            return ResponseEntity.ok(paymentService.getRazorpayOrderDetails(razorpayOrderId));
//////        } catch (RazorpayException e) {
//////            return ResponseEntity.badRequest().body("Error fetching Razorpay order: " + e.getMessage());
//////        }
//////    }
//////
//////    @PostMapping("/capture/{paymentId}")
//////    public ResponseEntity<?> capturePayment(@PathVariable String paymentId, @RequestParam BigDecimal amount) {
//////        try {
//////            return ResponseEntity.ok(paymentService.capturePayment(paymentId, amount));
//////        } catch (RazorpayException e) {
//////            return ResponseEntity.badRequest().body("Error capturing payment: " + e.getMessage());
//////        }
//////    }
//////}
////
////
////package com.ex.razorpay.controller;
////
////import com.ex.razorpay.dto.*;
////import com.ex.razorpay.entity.Order;
////import com.ex.razorpay.service.PaymentService;
////import com.razorpay.RazorpayException;
////import lombok.RequiredArgsConstructor;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////import java.math.BigDecimal;
////
////@RestController
////@RequestMapping("/api/payments")
////@RequiredArgsConstructor
////public class PaymentController {
////
////    private final PaymentService paymentService;
////
////    @PostMapping("/create-order")
////    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
////        try {
////            OrderResponse response = paymentService.createOrder(request);
////            return ResponseEntity.ok(response);
////        } catch (RazorpayException e) {
////            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
////        } catch (Exception e) {
////            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
////        }
////    }
////
////    @PostMapping("/verify")
////    public ResponseEntity<?> verifyPayment(@RequestBody PaymentRequest request) {
////        try {
////            PaymentResponse response = paymentService.verifyPayment(request);
////            return ResponseEntity.ok(response);
////        } catch (Exception e) {
////            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
////        }
////    }
////
////    @GetMapping("/order/{orderId}")
////    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {
////        try {
////            return paymentService.getOrderByOrderId(orderId)
////                    .map(ResponseEntity::ok)
////                    .orElse(ResponseEntity.notFound().build());
////        } catch (Exception e) {
////            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
////        }
////    }
////    
////    @GetMapping("/razorpay-order/{razorpayOrderId}")
////    public ResponseEntity<?> getRazorpayOrderDetails(@PathVariable String razorpayOrderId) {
////        try {
////            return ResponseEntity.ok(paymentService.getRazorpayOrderDetails(razorpayOrderId));
////        } catch (RazorpayException e) {
////            return ResponseEntity.badRequest().body("Error fetching Razorpay order: " + e.getMessage());
////        }
////    }
////    
////    @PostMapping("/capture/{paymentId}")
////    public ResponseEntity<?> capturePayment(@PathVariable String paymentId, @RequestParam BigDecimal amount) {
////        try {
////            return ResponseEntity.ok(paymentService.capturePayment(paymentId, amount));
////        } catch (RazorpayException e) {
////            return ResponseEntity.badRequest().body("Error capturing payment: " + e.getMessage());
////        }
////    }
////}
//
//
//package com.ex.razorpay.controller;
//
//import com.ex.razorpay.dto.*;
//import com.ex.razorpay.entity.Order;
//import com.ex.razorpay.entity.Payment;
//import com.ex.razorpay.enumm.PaymentStatus;
//import com.ex.razorpay.service.PaymentService;
//import com.razorpay.RazorpayException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/payments")
//@RequiredArgsConstructor
//public class PaymentController {
//
//    private final PaymentService paymentService;
//    
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
//        return paymentService.getPaymentById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/payment-id/{paymentId}")
//    public ResponseEntity<?> getPaymentByPaymentId(@PathVariable String paymentId) {
//        return paymentService.getPaymentByPaymentId(paymentId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//    @GetMapping("/razorpay-id/{razorpayPaymentId}")
//    public ResponseEntity<?> getPaymentByRazorpayPaymentId(@PathVariable String razorpayPaymentId) {
//        return paymentService.getPaymentByRazorpayPaymentId(razorpayPaymentId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//    @GetMapping
//    public ResponseEntity<List<Payment>> getAllPayments() {
//        return ResponseEntity.ok(paymentService.getAllPayments());
//    }
//
//    @GetMapping("/status/{status}")
//    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
//        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
//    }
//
//    @GetMapping("/order/{orderId}")
//    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable Long orderId) {
//        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
//    }
//
//    @PutMapping("/{id}/status")
//    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id, 
//                                               @RequestParam PaymentStatus status) {
//        try {
//            Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
//            return ResponseEntity.ok(updatedPayment);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.badRequest().body(error);
//        }
//    }
//    
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
//        try {
//            paymentService.deletePayment(id);
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Payment deleted successfully");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.badRequest().body(error);
//        }
//    }
//    
//    @GetMapping("/statistics")
//    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
//        return ResponseEntity.ok(paymentService.getPaymentStatistics());
//    }
//
//
//    @PostMapping("/create-order")
//    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
//        try {
//            OrderResponse response = paymentService.createOrder(request);
//            return ResponseEntity.ok(response);
//        } catch (RazorpayException e) {
//            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/verify")
//    public ResponseEntity<?> verifyPayment(@RequestBody PaymentRequest request) {
//        try {
//            PaymentResponse response = paymentService.verifyPayment(request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/order/{orderIdd}")
//    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {
//        try {
//            return paymentService.getOrderByOrderId(orderId)
//                    .map(ResponseEntity::ok)
//                    .orElse(ResponseEntity.notFound().build());
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//    
//    @GetMapping("/razorpay-order/{razorpayOrderId}")
//    public ResponseEntity<?> getRazorpayOrderDetails(@PathVariable String razorpayOrderId) {
//        try {
//            return ResponseEntity.ok(paymentService.getRazorpayOrderDetails(razorpayOrderId));
//        } catch (RazorpayException e) {
//            return ResponseEntity.badRequest().body("Error fetching Razorpay order: " + e.getMessage());
//        }
//    }
//    
//    @PostMapping("/capture/{paymentId}")
//    public ResponseEntity<?> capturePayment(@PathVariable String paymentId, 
//                                          @RequestParam BigDecimal amount) {
//        try {
//            return ResponseEntity.ok(paymentService.capturePayment(paymentId, amount));
//        } catch (RazorpayException e) {
//            return ResponseEntity.badRequest().body("Error capturing payment: " + e.getMessage());
//        }
//    }
//    
//    @GetMapping("/payment/{paymentId}")
//    public ResponseEntity<?> getPaymentDetails(@PathVariable String paymentId) {
//        try {
//            return ResponseEntity.ok(paymentService.getPaymentDetails(paymentId));
//        } catch (RazorpayException e) {
//            return ResponseEntity.badRequest().body("Error fetching payment: " + e.getMessage());
//        }
//    }
//    
//    @PostMapping("/refund/{paymentId}")
//    public ResponseEntity<?> refundPayment(@PathVariable String paymentId,
//                                         @RequestParam BigDecimal amount) {
//        try {
//            return ResponseEntity.ok(paymentService.refundPayment(paymentId, amount));
//        } catch (RazorpayException e) {
//            return ResponseEntity.badRequest().body("Error processing refund: " + e.getMessage());
//        }
//    }
//}


package com.ex.razorpay.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.ex.razorpay.dto.*;
import com.ex.razorpay.entity.Order;
import com.ex.razorpay.entity.Payment;
import com.ex.razorpay.entity.User;
import com.ex.razorpay.enumm.PaymentStatus;
import com.ex.razorpay.repository.PaymentRepository;
import com.ex.razorpay.repository.UserRepository;
import com.ex.razorpay.service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    
    // Payment CRUD Operations
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/payment-id/{paymentId}")
    public ResponseEntity<?> getPaymentByPaymentId(@PathVariable String paymentId) {
        return paymentService.getPaymentByPaymentId(paymentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/razorpay-id/{razorpayPaymentId}")
    public ResponseEntity<?> getPaymentByRazorpayPaymentId(@PathVariable String razorpayPaymentId) {
        return paymentService.getPaymentByRazorpayPaymentId(razorpayPaymentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
//    @GetMapping
//    public ResponseEntity<List<Payment>> getAllPayments() {
//        return ResponseEntity.ok(paymentService.getAllPayments());
//    }

    // Add @PreAuthorize annotations to existing methods
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }

    @GetMapping("/payments-by-order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id, 
                                               @RequestParam PaymentStatus status) {
        try {
            Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Payment deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
//        // ... existing code
//    }
//    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
        return ResponseEntity.ok(paymentService.getPaymentStatistics());
    }

    // Razorpay Integration APIs
    @PostMapping("/create-order")
    public ResponseEntity<?> createRazorpayOrder(@RequestBody CreateOrderRequest request) {
        try {
            OrderResponse response = paymentService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.verifyPayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/order-details/{orderId}")
    public ResponseEntity<?> getOrderDetailsByOrderId(@PathVariable String orderId) {
        try {
            return paymentService.getOrderByOrderId(orderId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/razorpay-order/{razorpayOrderId}")
    public ResponseEntity<?> getRazorpayOrderDetails(@PathVariable String razorpayOrderId) {
        try {
            return ResponseEntity.ok(paymentService.getRazorpayOrderDetails(razorpayOrderId));
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body("Error fetching Razorpay order: " + e.getMessage());
        }
    }
    
    @PostMapping("/capture/{paymentId}")
    public ResponseEntity<?> capturePayment(@PathVariable String paymentId, 
                                          @RequestParam BigDecimal amount) {
        try {
            return ResponseEntity.ok(paymentService.capturePayment(paymentId, amount));
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body("Error capturing payment: " + e.getMessage());
        }
    }
    
    @GetMapping("/payment-details/{paymentId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable String paymentId) {
        try {
            return ResponseEntity.ok(paymentService.getPaymentDetails(paymentId));
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body("Error fetching payment: " + e.getMessage());
        }
    }
    
    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<?> refundPayment(@PathVariable String paymentId,
                                         @RequestParam BigDecimal amount) {
        try {
            return ResponseEntity.ok(paymentService.refundPayment(paymentId, amount));
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body("Error processing refund: " + e.getMessage());
        }
    }
    

 // Add this method to get current user's payments
 @GetMapping("/my-payments")
 @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
 public ResponseEntity<?> getMyPayments(Authentication authentication) {
     try {
         UserDetails userDetails = (UserDetails) authentication.getPrincipal();
         String email = userDetails.getUsername();
         
         // Get user by email and then their payments
         User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new RuntimeException("User not found"));
         
         List<Payment> userPayments = paymentRepository.findAll().stream()
                 .filter(payment -> payment.getOrder().getUser().getId().equals(user.getId()))
                 .toList();
         
         return ResponseEntity.ok(userPayments);
     } catch (Exception e) {
         Map<String, String> error = new HashMap<>();
         error.put("error", e.getMessage());
         return ResponseEntity.badRequest().body(error);
     }
 }





}