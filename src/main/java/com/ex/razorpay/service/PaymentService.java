//////package com.ex.razorpay.service;
//////
//////import com.ex.razorpay.dto.CreateOrderRequest;
//////import com.ex.razorpay.dto.OrderResponse;
//////import com.ex.razorpay.dto.PaymentRequest;
//////import com.ex.razorpay.dto.PaymentResponse;
//////import com.ex.razorpay.entity.*;
//////import com.ex.razorpay.repository.*;
//////import com.ex.razorpay.enumm.OrderStatus;
//////import com.ex.razorpay.enumm.PaymentStatus;
//////import com.razorpay.RazorpayClient;
//////import com.razorpay.RazorpayException;
//////import jakarta.transaction.Transactional;
//////import lombok.RequiredArgsConstructor;
//////import org.json.JSONObject;
//////import org.jspecify.annotations.Nullable;
//////import org.springframework.beans.factory.annotation.Value;
//////import org.springframework.stereotype.Service;
//////import java.math.BigDecimal;
//////import java.util.List;
//////import java.util.UUID;
//////
//////@Service
//////@RequiredArgsConstructor
//////public class PaymentService {
//////
//////    private final RazorpayClient razorpayClient;
//////    private final UserRepository userRepository;
//////    private final ProductRepository productRepository;
//////    private final OrderRepository orderRepository;
//////    private final PaymentRepository paymentRepository;
//////
//////    @Value("${razorpay.key.secret}")
//////    private String razorpaySecret;
//////
//////    /* ---------------- CREATE ORDER ---------------- */
//////
//////    @Transactional
//////    public OrderResponse createOrder(CreateOrderRequest request) throws RazorpayException {
//////
//////        User user = userRepository.findById(request.getUserId())
//////                .orElseThrow(() -> new RuntimeException("User not found"));
//////
//////        BigDecimal totalAmount = BigDecimal.ZERO;
//////
//////        for (var item : request.getItems()) {
//////            Product product = productRepository.findById(item.getProductId())
//////                    .orElseThrow(() -> new RuntimeException("Product not found"));
//////            totalAmount = totalAmount.add(
//////                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
//////            );
//////        }
//////
//////        JSONObject razorpayRequest = new JSONObject();
//////        razorpayRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue());
//////        razorpayRequest.put("currency", "INR");
//////        razorpayRequest.put("receipt", generateOrderId());
//////        razorpayRequest.put("payment_capture", 1);
//////
//////        com.razorpay.Order razorpayOrder =
//////                razorpayClient.orders.create(razorpayRequest);
//////
//////        Order order = new Order();
//////        order.setUser(user);
//////        order.setOrderId(razorpayRequest.getString("receipt"));
//////        order.setRazorpayOrderId(razorpayOrder.get("id"));
//////        order.setAmount(totalAmount);
//////        order.setStatus(OrderStatus.PENDING);
//////
//////        orderRepository.save(order);
//////
//////        return new OrderResponse(
//////                order.getOrderId(),
//////                order.getRazorpayOrderId(),
//////                order.getAmount(),
//////                "INR",
//////                order.getStatus().name()
//////        );
//////    }
//////
//////    /* ---------------- VERIFY PAYMENT ---------------- */
//////
//////    @Transactional
//////    public PaymentResponse verifyPayment(PaymentRequest request) {
//////
//////        Order order = orderRepository.findByOrderId(request.getOrderId())
//////                .orElseThrow(() -> new RuntimeException("Order not found"));
//////
//////        String expectedSignature = generateSignature(
//////                order.getRazorpayOrderId(),
//////                request.getPaymentId(),
//////                razorpaySecret
//////        );
//////
//////        if (!expectedSignature.equals(request.getSignature())) {
//////            order.setStatus(OrderStatus.FAILED);
//////            orderRepository.save(order);
//////            throw new RuntimeException("Invalid payment signature");
//////        }
//////
//////        Payment payment = new Payment();
//////        payment.setPaymentId(generatePaymentId());
//////        payment.setRazorpayPaymentId(request.getPaymentId());
//////        payment.setRazorpayOrderId(order.getRazorpayOrderId());
//////        payment.setRazorpaySignature(request.getSignature());
//////        payment.setAmount(request.getAmount());
//////        payment.setCurrency("INR");
//////        payment.setStatus(PaymentStatus.SUCCESS);
//////        payment.setPaymentMethod("RAZORPAY");
//////        payment.setOrder(order);
//////
//////        paymentRepository.save(payment);
//////
//////        order.setStatus(OrderStatus.COMPLETED);
//////        orderRepository.save(order);
//////
//////        return new PaymentResponse(
//////                payment.getPaymentId(),
//////                payment.getRazorpayPaymentId(),
//////                payment.getAmount(),
//////                payment.getStatus().name(),
//////                "Payment successful"
//////        );
//////    }
//////
//////    /* ---------------- FETCH ORDER ---------------- */
//////
//////    public Order getOrderByOrderId(String orderId) {
//////        return orderRepository.findByOrderId(orderId)
//////                .orElseThrow(() -> new RuntimeException("Order not found"));
//////    }
//////
//////    /* ---------------- UTILS ---------------- */
//////
//////    private String generateOrderId() {
//////        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//////    }
//////
//////    private String generatePaymentId() {
//////        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//////    }
//////
//////    private String generateSignature(String orderId, String paymentId, String secret) {
//////        try {
//////            String data = orderId + "|" + paymentId;
//////            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
//////            mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256"));
//////            return bytesToHex(mac.doFinal(data.getBytes()));
//////        } catch (Exception e) {
//////            throw new RuntimeException("Signature generation failed", e);
//////        }
//////    }
//////
//////    private String bytesToHex(byte[] bytes) {
//////        StringBuilder sb = new StringBuilder();
//////        for (byte b : bytes) sb.append(String.format("%02x", b));
//////        return sb.toString();
//////    }
//////}
////
////
////package com.ex.razorpay.service;
////
////import com.ex.razorpay.dto.CreateOrderRequest;
////import com.ex.razorpay.dto.OrderItemDTO;
////import com.ex.razorpay.dto.OrderResponse;
////import com.ex.razorpay.dto.PaymentRequest;
////import com.ex.razorpay.dto.PaymentResponse;
////import com.ex.razorpay.entity.*;
////import com.ex.razorpay.repository.*;
////import com.ex.razorpay.enumm.OrderStatus;
////import com.ex.razorpay.enumm.PaymentStatus;
////import com.razorpay.RazorpayClient;
////import com.razorpay.RazorpayException;
////import jakarta.transaction.Transactional;
////import lombok.RequiredArgsConstructor;
////import org.json.JSONObject;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.stereotype.Service;
////
////import java.math.BigDecimal;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////import java.util.Optional;
////import java.util.UUID;
////
////@Service
////@RequiredArgsConstructor
////public class PaymentService {
////
////    private final RazorpayClient razorpayClient;
////    private final UserRepository userRepository;
////    private final ProductRepository productRepository;
////    private final OrderRepository orderRepository;
////    private final PaymentRepository paymentRepository;
////    
////    @Value("${razorpay.key.secret}")
////    private String razorpaySecret;
////
////    @Transactional
////    public OrderResponse createOrder(CreateOrderRequest request) throws RazorpayException {
////        // Get user
////        User user = userRepository.findById(request.getUserId())
////                .orElseThrow(() -> new RuntimeException("User not found"));
////
////        // Calculate total amount first
////        BigDecimal totalAmount = BigDecimal.ZERO;
////        for (OrderItemDTO item : request.getItems()) {
////            Product product = productRepository.findById(item.getProductId())
////                    .orElseThrow(() -> new RuntimeException("Product not found"));
////            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
////        }
////
////        // Create Razorpay order
////        JSONObject orderRequest = new JSONObject();
////        orderRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue()); // Convert to paise
////        orderRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : "INR");
////        String customOrderId = generateOrderId();
////        orderRequest.put("receipt", customOrderId);
////        orderRequest.put("payment_capture", 1);
////
////        // Create order in Razorpay
////        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
////        
////        // Save order to database
////        Order order = new Order();
////        order.setUser(user);
////        order.setOrderId(customOrderId);
////        order.setRazorpayOrderId(razorpayOrder.get("id"));
////        order.setAmount(totalAmount);
////        order.setStatus(OrderStatus.PENDING);
////        
////        // Save order items
////        for (OrderItemDTO item : request.getItems()) {
////            Product product = productRepository.findById(item.getProductId())
////                    .orElseThrow(() -> new RuntimeException("Product not found"));
////            
////            OrderItem orderItem = new OrderItem();
////            orderItem.setOrder(order);
////            orderItem.setProduct(product);
////            orderItem.setQuantity(item.getQuantity());
////            orderItem.setUnitPrice(product.getPrice());
////            
////            order.getOrderItems().add(orderItem);
////        }
////        
////        orderRepository.save(order);
////
////        // Prepare response
////        OrderResponse response = new OrderResponse();
////        response.setOrderId(order.getOrderId());
////        response.setRazorpayOrderId(order.getRazorpayOrderId());
////        response.setAmount(order.getAmount());
////        response.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
////        response.setStatus(order.getStatus().toString());
////        
////        return response;
////    }
////
////    @Transactional
////    public PaymentResponse verifyPayment(PaymentRequest request) {
////        try {
////            // Get order
////            Order order = orderRepository.findByOrderId(request.getOrderId())
////                    .orElseThrow(() -> new RuntimeException("Order not found"));
////
////            // Verify signature
////            String generatedSignature = generateSignature(
////                    order.getRazorpayOrderId(), 
////                    request.getPaymentId(), 
////                    razorpaySecret
////            );
////
////            if (!generatedSignature.equals(request.getSignature())) {
////                throw new RuntimeException("Payment signature verification failed");
////            }
////
////            // Create payment record
////            Payment payment = new Payment();
////            payment.setPaymentId(generatePaymentId());
////            payment.setRazorpayPaymentId(request.getPaymentId());
////            payment.setRazorpayOrderId(order.getRazorpayOrderId());
////            payment.setRazorpaySignature(request.getSignature());
////            payment.setAmount(request.getAmount());
////            payment.setCurrency(request.getCurrency());
////            payment.setOrder(order);
////            payment.setStatus(PaymentStatus.SUCCESS);
////            payment.setPaymentMethod("RAZORPAY");
////
////            paymentRepository.save(payment);
////
////            // Update order status
////            order.setStatus(OrderStatus.COMPLETED);
////            orderRepository.save(order);
////
////            PaymentResponse response = new PaymentResponse();
////            response.setPaymentId(payment.getPaymentId());
////            response.setRazorpayPaymentId(payment.getRazorpayPaymentId());
////            response.setAmount(payment.getAmount());
////            response.setStatus(payment.getStatus().toString());
////            response.setMessage("Payment successful");
////
////            return response;
////
////        } catch (Exception e) {
////            // Update order status to failed
////            orderRepository.findByOrderId(request.getOrderId()).ifPresent(order -> {
////                order.setStatus(OrderStatus.FAILED);
////                orderRepository.save(order);
////            });
////
////            PaymentResponse response = new PaymentResponse();
////            response.setStatus("FAILED");
////            response.setMessage("Payment verification failed: " + e.getMessage());
////            
////            return response;
////        }
////    }
////    
////    // Add method to get order by orderId
////    public Optional<Order> getOrderByOrderId(String orderId) {
////        return orderRepository.findByOrderId(orderId);
////    }
////    
////    // Add method to get order by razorpay order ID
////    public Optional<Order> getOrderByRazorpayOrderId(String razorpayOrderId) {
////        return orderRepository.findByRazorpayOrderId(razorpayOrderId);
////    }
////
////    // Optional: Method to get order details from Razorpay
////    public Map<String, Object> getRazorpayOrderDetails(String razorpayOrderId) throws RazorpayException {
////        com.razorpay.Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);
////        return razorpayOrder.toMap();
////    }
////    
////    // Optional: Method to capture payment (if manual capture is enabled)
////    public Map<String, Object> capturePayment(String paymentId, BigDecimal amount) throws RazorpayException {
////        JSONObject captureRequest = new JSONObject();
////        captureRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
////        captureRequest.put("currency", "INR");
////        com.razorpay.Payment payment = razorpayClient.payments.capture(paymentId, captureRequest);
////        return payment.toMap();
////    }
////    
////    private Map<String, Object> jsonToMap(JSONObject json) {
////        Map<String, Object> map = new HashMap<>();
////        for (String key : json.keySet()) {
////            map.put(key, json.get(key));
////        }
////        return map;
////    }
////
////
////    private String generateOrderId() {
////        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + 
////               "-" + System.currentTimeMillis();
////    }
////
////    private String generatePaymentId() {
////        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + 
////               "-" + System.currentTimeMillis();
////    }
////
////    private String generateSignature(String orderId, String paymentId, String secret) {
////        String data = orderId + "|" + paymentId;
////        try {
////            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
////            javax.crypto.spec.SecretKeySpec secretKeySpec = 
////                    new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256");
////            mac.init(secretKeySpec);
////            byte[] signature = mac.doFinal(data.getBytes());
////            return bytesToHex(signature);
////        } catch (Exception e) {
////            throw new RuntimeException("Error generating signature", e);
////        }
////    }
////
////    private String bytesToHex(byte[] bytes) {
////        StringBuilder result = new StringBuilder();
////        for (byte b : bytes) {
////            result.append(String.format("%02x", b));
////        }
////        return result.toString();
////    }
////}
//
//
//package com.ex.razorpay.service;
//
//import com.ex.razorpay.dto.*;
//import com.ex.razorpay.entity.*;
//import com.ex.razorpay.repository.*;
//import com.ex.razorpay.enumm.OrderStatus;
//import com.ex.razorpay.enumm.PaymentStatus;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentService {
//
//    private final RazorpayClient razorpayClient;
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//    private final OrderRepository orderRepository;
//    private final PaymentRepository paymentRepository;
//    
//    @Value("${razorpay.key.secret}")
//    private String razorpaySecret;
//    
//    // Get payment by ID
//    public Optional<Payment> getPaymentById(Long id) {
//        return paymentRepository.findById(id);
//    }
//    
//    // Get payment by payment ID
//    public Optional<Payment> getPaymentByPaymentId(String paymentId) {
//        return paymentRepository.findByPaymentId(paymentId);
//    }
//    // Get payment by Razorpay payment ID
//    public Optional<Payment> getPaymentByRazorpayPaymentId(String razorpayPaymentId) {
//        return paymentRepository.findByRazorpayPaymentId(razorpayPaymentId);
//    }
//    public List<Payment> getAllPayments() {
//        return paymentRepository.findAll();
//    }
//    // Get payments by status
//    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
//        return paymentRepository.findAll().stream()
//                .filter(payment -> payment.getStatus() == status)
//                .toList();
//    }
//    // Get payments by order ID
//    public List<Payment> getPaymentsByOrderId(Long orderId) {
//        return paymentRepository.findAll().stream()
//                .filter(payment -> payment.getOrder().getId().equals(orderId))
//                .toList();
//    }
//
//    // Update payment status
//    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
//        Payment payment = paymentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
//
//        payment.setStatus(status);
//        return paymentRepository.save(payment);
//    }
//
//    // Delete payment
//    public void deletePayment(Long id) {
//        if (!paymentRepository.existsById(id)) {
//            throw new RuntimeException("Payment not found with id: " + id);
//        }
//        paymentRepository.deleteById(id);
//    }
//    // Get payment statistics
//    public Map<String, Object> getPaymentStatistics() {
//        List<Payment> allPayments = paymentRepository.findAll();
//        
//        long totalPayments = allPayments.size();
//        long successfulPayments = allPayments.stream()
//                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
//                .count();
//        long failedPayments = allPayments.stream()
//                .filter(p -> p.getStatus() == PaymentStatus.FAILED)
//                .count();
//        long pendingPayments = allPayments.stream()
//                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
//                .count();
//        
//        BigDecimal totalAmount = allPayments.stream()
//                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
//                .map(Payment::getAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("totalPayments", totalPayments);
//        stats.put("successfulPayments", successfulPayments);
//        stats.put("failedPayments", failedPayments);
//        stats.put("pendingPayments", pendingPayments);
//        stats.put("totalAmount", totalAmount);
//        
//        return stats;
//    }
//
//    @Transactional
//    public OrderResponse createOrder(CreateOrderRequest request) throws RazorpayException {
//        // Get user
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Calculate total amount first
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        for (OrderItemDTO item : request.getItems()) {
//            Product product = productRepository.findById(item.getProductId())
//                    .orElseThrow(() -> new RuntimeException("Product not found"));
//            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
//        }
//
//        // Create Razorpay order
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue()); // Convert to paise
//        orderRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : "INR");
//        String customOrderId = generateOrderId();
//        orderRequest.put("receipt", customOrderId);
//        orderRequest.put("payment_capture", 1);
//
//        // Create order in Razorpay
//        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
//        
//        // Save order to database
//        Order order = new Order();
//        order.setUser(user);
//        order.setOrderId(customOrderId);
//        order.setRazorpayOrderId(razorpayOrder.get("id"));
//        order.setAmount(totalAmount);
//        order.setStatus(OrderStatus.PENDING);
//        
//        // Save order items
//        for (OrderItemDTO item : request.getItems()) {
//            Product product = productRepository.findById(item.getProductId())
//                    .orElseThrow(() -> new RuntimeException("Product not found"));
//            
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProduct(product);
//            orderItem.setQuantity(item.getQuantity());
//            orderItem.setUnitPrice(product.getPrice());
//            
//            order.getOrderItems().add(orderItem);
//        }
//        
//        orderRepository.save(order);
//
//        // Prepare response
//        OrderResponse response = new OrderResponse();
//        response.setOrderId(order.getOrderId());
//        response.setRazorpayOrderId(order.getRazorpayOrderId());
//        response.setAmount(order.getAmount());
//        response.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
//        response.setStatus(order.getStatus().toString());
//        
//        return response;
//    }
//
//    @Transactional
//    public PaymentResponse verifyPayment(PaymentRequest request) {
//        try {
//            // Get order
//            Order order = orderRepository.findByOrderId(request.getOrderId())
//                    .orElseThrow(() -> new RuntimeException("Order not found"));
//
//            // Verify signature
//            String generatedSignature = generateSignature(
//                    order.getRazorpayOrderId(), 
//                    request.getPaymentId(), 
//                    razorpaySecret
//            );
//
//            if (!generatedSignature.equals(request.getSignature())) {
//                throw new RuntimeException("Payment signature verification failed");
//            }
//
//            // Create payment record
//            Payment payment = new Payment();
//            payment.setPaymentId(generatePaymentId());
//            payment.setRazorpayPaymentId(request.getPaymentId());
//            payment.setRazorpayOrderId(order.getRazorpayOrderId());
//            payment.setRazorpaySignature(request.getSignature());
//            payment.setAmount(request.getAmount());
//            payment.setCurrency(request.getCurrency());
//            payment.setOrder(order);
//            payment.setStatus(PaymentStatus.SUCCESS);
//            payment.setPaymentMethod("RAZORPAY");
//
//            paymentRepository.save(payment);
//
//            // Update order status
//            order.setStatus(OrderStatus.COMPLETED);
//            orderRepository.save(order);
//
//            PaymentResponse response = new PaymentResponse();
//            response.setPaymentId(payment.getPaymentId());
//            response.setRazorpayPaymentId(payment.getRazorpayPaymentId());
//            response.setAmount(payment.getAmount());
//            response.setStatus(payment.getStatus().toString());
//            response.setMessage("Payment successful");
//
//            return response;
//
//        } catch (Exception e) {
//            // Update order status to failed
//            orderRepository.findByOrderId(request.getOrderId()).ifPresent(order -> {
//                order.setStatus(OrderStatus.FAILED);
//                orderRepository.save(order);
//            });
//
//            PaymentResponse response = new PaymentResponse();
//            response.setStatus("FAILED");
//            response.setMessage("Payment verification failed: " + e.getMessage());
//            
//            return response;
//        }
//    }
//    
//    // Method to get order by orderId
//    public Optional<Order> getOrderByOrderId(String orderId) {
//        return orderRepository.findByOrderId(orderId);
//    }
//    
//    // Method to get order by razorpay order ID
//    public Optional<Order> getOrderByRazorpayOrderId(String razorpayOrderId) {
//        return orderRepository.findByRazorpayOrderId(razorpayOrderId);
//    }
//
//    // Method to get order details from Razorpay - FIXED VERSION
//    public Map<String, Object> getRazorpayOrderDetails(String razorpayOrderId) throws RazorpayException {
//        com.razorpay.Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);
//        return convertOrderToMap(razorpayOrder);
//    }
//    
//    // Method to capture payment (if manual capture is enabled) - FIXED VERSION
//    public Map<String, Object> capturePayment(String paymentId, BigDecimal amount) throws RazorpayException {
//        JSONObject captureRequest = new JSONObject();
//        captureRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
//        captureRequest.put("currency", "INR");
//        com.razorpay.Payment payment = razorpayClient.payments.capture(paymentId, captureRequest);
//        return convertPaymentToMap(payment);
//    }
//    
//    // Helper method to convert Razorpay Order to Map
//    private Map<String, Object> convertOrderToMap(com.razorpay.Order razorpayOrder) {
//        Map<String, Object> orderMap = new HashMap<>();
//        
//        // Extract common fields from Razorpay Order
//        orderMap.put("id", razorpayOrder.get("id"));
//        orderMap.put("amount", razorpayOrder.get("amount"));
//        orderMap.put("currency", razorpayOrder.get("currency"));
//        orderMap.put("receipt", razorpayOrder.get("receipt"));
//        orderMap.put("status", razorpayOrder.get("status"));
//        orderMap.put("attempts", razorpayOrder.get("attempts"));
//        orderMap.put("created_at", razorpayOrder.get("created_at"));
//        
//        return orderMap;
//    }
//    
//    // Helper method to convert Razorpay Payment to Map
//    private Map<String, Object> convertPaymentToMap(com.razorpay.Payment razorpayPayment) {
//        Map<String, Object> paymentMap = new HashMap<>();
//        
//        // Extract common fields from Razorpay Payment
//        paymentMap.put("id", razorpayPayment.get("id"));
//        paymentMap.put("amount", razorpayPayment.get("amount"));
//        paymentMap.put("currency", razorpayPayment.get("currency"));
//        paymentMap.put("status", razorpayPayment.get("status"));
//        paymentMap.put("method", razorpayPayment.get("method"));
//        paymentMap.put("order_id", razorpayPayment.get("order_id"));
//        paymentMap.put("created_at", razorpayPayment.get("created_at"));
//        paymentMap.put("captured", razorpayPayment.get("captured"));
//        
//        return paymentMap;
//    }
//
//    private String generateOrderId() {
//        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + 
//               "-" + System.currentTimeMillis();
//    }
//
//    private String generatePaymentId() {
//        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + 
//               "-" + System.currentTimeMillis();
//    }
//
//    private String generateSignature(String orderId, String paymentId, String secret) {
//        String data = orderId + "|" + paymentId;
//        try {
//            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
//            javax.crypto.spec.SecretKeySpec secretKeySpec = 
//                    new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256");
//            mac.init(secretKeySpec);
//            byte[] signature = mac.doFinal(data.getBytes());
//            return bytesToHex(signature);
//        } catch (Exception e) {
//            throw new RuntimeException("Error generating signature", e);
//        }
//    }
//
//    private String bytesToHex(byte[] bytes) {
//        StringBuilder result = new StringBuilder();
//        for (byte b : bytes) {
//            result.append(String.format("%02x", b));
//        }
//        return result.toString();
//    }
//    
//    // Additional utility methods
//    
//    public Map<String, Object> getPaymentDetails(String paymentId) throws RazorpayException {
//        com.razorpay.Payment payment = razorpayClient.payments.fetch(paymentId);
//        return convertPaymentToMap(payment);
//    }
//    
//    public Map<String, Object> refundPayment(String paymentId, BigDecimal amount) throws RazorpayException {
//        JSONObject refundRequest = new JSONObject();
//        refundRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
//        
//        com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);
//        
//        Map<String, Object> refundMap = new HashMap<>();
//        refundMap.put("id", refund.get("id"));
//        refundMap.put("amount", refund.get("amount"));
//        refundMap.put("currency", refund.get("currency"));
//        refundMap.put("payment_id", refund.get("payment_id"));
//        refundMap.put("status", refund.get("status"));
//        
//        return refundMap;
//    }
//}



package com.ex.razorpay.service;

import com.ex.razorpay.dto.*;
import com.ex.razorpay.entity.*;
import com.ex.razorpay.repository.*;
import com.ex.razorpay.enumm.OrderStatus;
import com.ex.razorpay.enumm.PaymentStatus;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;
//    
//    
//  private final RazorpayClient razorpayClient;
//  private final UserRepository userRepository;
//  private final ProductRepository productRepository;
//  private final OrderRepository orderRepository;
//  private final PaymentRepository paymentRepository;
//  
//  @Value("${razorpay.key.secret}")
//  private String razorpaySecret;
  
  // Get payment by ID
  public Optional<Payment> getPaymentById(Long id) {
      return paymentRepository.findById(id);
  }
  
  // Get payment by payment ID
  public Optional<Payment> getPaymentByPaymentId(String paymentId) {
      return paymentRepository.findByPaymentId(paymentId);
  }
  // Get payment by Razorpay payment ID
  public Optional<Payment> getPaymentByRazorpayPaymentId(String razorpayPaymentId) {
      return paymentRepository.findByRazorpayPaymentId(razorpayPaymentId);
  }
  public List<Payment> getAllPayments() {
      return paymentRepository.findAll();
  }
  // Get payments by status
  public List<Payment> getPaymentsByStatus(PaymentStatus status) {
      return paymentRepository.findAll().stream()
              .filter(payment -> payment.getStatus() == status)
              .toList();
  }
  // Get payments by order ID
  public List<Payment> getPaymentsByOrderId(Long orderId) {
      return paymentRepository.findAll().stream()
              .filter(payment -> payment.getOrder().getId().equals(orderId))
              .toList();
  }

  // Update payment status
  public Payment updatePaymentStatus(Long id, PaymentStatus status) {
      Payment payment = paymentRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

      payment.setStatus(status);
      return paymentRepository.save(payment);
  }

  // Delete payment
  public void deletePayment(Long id) {
      if (!paymentRepository.existsById(id)) {
          throw new RuntimeException("Payment not found with id: " + id);
      }
      paymentRepository.deleteById(id);
  }
  // Get payment statistics
  public Map<String, Object> getPaymentStatistics() {
      List<Payment> allPayments = paymentRepository.findAll();
      
      long totalPayments = allPayments.size();
      long successfulPayments = allPayments.stream()
              .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
              .count();
      long failedPayments = allPayments.stream()
              .filter(p -> p.getStatus() == PaymentStatus.FAILED)
              .count();
      long pendingPayments = allPayments.stream()
              .filter(p -> p.getStatus() == PaymentStatus.PENDING)
              .count();
      
      BigDecimal totalAmount = allPayments.stream()
              .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
              .map(Payment::getAmount)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      Map<String, Object> stats = new HashMap<>();
      stats.put("totalPayments", totalPayments);
      stats.put("successfulPayments", successfulPayments);
      stats.put("failedPayments", failedPayments);
      stats.put("pendingPayments", pendingPayments);
      stats.put("totalAmount", totalAmount);
      
      return stats;
  }

  @Transactional
  public OrderResponse createOrder(CreateOrderRequest request) throws RazorpayException {
      // Get user
      User user = userRepository.findById(request.getUserId())
              .orElseThrow(() -> new RuntimeException("User not found"));

      // Calculate total amount first
      BigDecimal totalAmount = BigDecimal.ZERO;
      for (OrderItemDTO item : request.getItems()) {
          Product product = productRepository.findById(item.getProductId())
                  .orElseThrow(() -> new RuntimeException("Product not found"));
          totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
      }

      // Create Razorpay order
      JSONObject orderRequest = new JSONObject();
      orderRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue()); // Convert to paise
      orderRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : "INR");
      String customOrderId = generateOrderId();
      orderRequest.put("receipt", customOrderId);
      orderRequest.put("payment_capture", 1);

      // Create order in Razorpay
      com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
      
      // Save order to database
      Order order = new Order();
      order.setUser(user);
      order.setOrderId(customOrderId);
      order.setRazorpayOrderId(razorpayOrder.get("id"));
      order.setAmount(totalAmount);
      order.setStatus(OrderStatus.PENDING);
      
      // Save order items
      for (OrderItemDTO item : request.getItems()) {
          Product product = productRepository.findById(item.getProductId())
                  .orElseThrow(() -> new RuntimeException("Product not found"));
          
          OrderItem orderItem = new OrderItem();
          orderItem.setOrder(order);
          orderItem.setProduct(product);
          orderItem.setQuantity(item.getQuantity());
          orderItem.setUnitPrice(product.getPrice());
          
          order.getOrderItems().add(orderItem);
      }
      
      orderRepository.save(order);

      // Prepare response
      OrderResponse response = new OrderResponse();
      response.setOrderId(order.getOrderId());
      response.setRazorpayOrderId(order.getRazorpayOrderId());
      response.setAmount(order.getAmount());
      response.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
      response.setStatus(order.getStatus().toString());
      
      return response;
  }

//  @Transactional
//  public PaymentResponse verifyPayment(PaymentRequest request) {
//      try {
//          // Get order
//          Order order = orderRepository.findByOrderId(request.getOrderId())
//                  .orElseThrow(() -> new RuntimeException("Order not found"));
//
//          // Verify signature
//          String generatedSignature = generateSignature(
//                  order.getRazorpayOrderId(), 
//                  request.getPaymentId(), 
//                  razorpaySecret
//          );
//
//          if (!generatedSignature.equals(request.getSignature())) {
//              throw new RuntimeException("Payment signature verification failed");
//          }
//
//          // Create payment record
//          Payment payment = new Payment();
//          payment.setPaymentId(generatePaymentId());
//          payment.setRazorpayPaymentId(request.getPaymentId());
//          payment.setRazorpayOrderId(order.getRazorpayOrderId());
//          payment.setRazorpaySignature(request.getSignature());
//          payment.setAmount(request.getAmount());
//          payment.setCurrency(request.getCurrency());
//          payment.setOrder(order);
//          payment.setStatus(PaymentStatus.SUCCESS);
//          payment.setPaymentMethod("RAZORPAY");
//
//          paymentRepository.save(payment);
//
//          // Update order status
//          order.setStatus(OrderStatus.COMPLETED);
//          orderRepository.save(order);
//
//          PaymentResponse response = new PaymentResponse();
//          response.setPaymentId(payment.getPaymentId());
//          response.setRazorpayPaymentId(payment.getRazorpayPaymentId());
//          response.setAmount(payment.getAmount());
//          response.setStatus(payment.getStatus().toString());
//          response.setMessage("Payment successful");
//
//          return response;
//
//      } catch (Exception e) {
//          // Update order status to failed
//          orderRepository.findByOrderId(request.getOrderId()).ifPresent(order -> {
//              order.setStatus(OrderStatus.FAILED);
//              orderRepository.save(order);
//          });
//
//          PaymentResponse response = new PaymentResponse();
//          response.setStatus("FAILED");
//          response.setMessage("Payment verification failed: " + e.getMessage());
//          
//          return response;
//      }
//  }
  
  @Transactional
  public PaymentResponse verifyPayment(PaymentRequest request) {
      try {
          // Get order
          Order order = orderRepository.findByOrderId(request.getOrderId())
                  .orElseThrow(() -> new RuntimeException("Order not found"));

          // Check if this is a test payment
          boolean isTestPayment = request.getPaymentId().startsWith("test_");
          
          if (!isTestPayment) {
              // Verify signature for real payments
              String generatedSignature = generateSignature(
                      order.getRazorpayOrderId(), 
                      request.getPaymentId(), 
                      razorpaySecret
              );

              if (!generatedSignature.equals(request.getSignature())) {
                  throw new RuntimeException("Payment signature verification failed");
              }
          }

          // Create payment record
          Payment payment = new Payment();
          payment.setPaymentId(generatePaymentId());
          payment.setRazorpayPaymentId(request.getPaymentId());
          payment.setRazorpayOrderId(order.getRazorpayOrderId());
          payment.setRazorpaySignature(request.getSignature());
          payment.setAmount(request.getAmount());
          payment.setCurrency(request.getCurrency());
          payment.setOrder(order);
          payment.setStatus(PaymentStatus.SUCCESS);
          payment.setPaymentMethod(isTestPayment ? "TEST" : "RAZORPAY");

          paymentRepository.save(payment);

          // Update order status
          order.setStatus(OrderStatus.COMPLETED);
          orderRepository.save(order);

          PaymentResponse response = new PaymentResponse();
          response.setPaymentId(payment.getPaymentId());
          response.setRazorpayPaymentId(payment.getRazorpayPaymentId());
          response.setAmount(payment.getAmount());
          response.setStatus(payment.getStatus().toString());
          response.setMessage(isTestPayment ? "Test payment successful" : "Payment successful");

          return response;

      } catch (Exception e) {
          // Update order status to failed
          orderRepository.findByOrderId(request.getOrderId()).ifPresent(order -> {
              order.setStatus(OrderStatus.FAILED);
              orderRepository.save(order);
          });

          PaymentResponse response = new PaymentResponse();
          response.setStatus("FAILED");
          response.setMessage("Payment verification failed: " + e.getMessage());
          
          return response;
      }
  }
  // Method to get order by orderId
  public Optional<Order> getOrderByOrderId(String orderId) {
      return orderRepository.findByOrderId(orderId);
  }
  
  // Method to get order by razorpay order ID
  public Optional<Order> getOrderByRazorpayOrderId(String razorpayOrderId) {
      return orderRepository.findByRazorpayOrderId(razorpayOrderId);
  }

  // Method to get order details from Razorpay - FIXED VERSION
  public Map<String, Object> getRazorpayOrderDetails(String razorpayOrderId) throws RazorpayException {
      com.razorpay.Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);
      return convertOrderToMap(razorpayOrder);
  }
  
  // Method to capture payment (if manual capture is enabled) - FIXED VERSION
  public Map<String, Object> capturePayment(String paymentId, BigDecimal amount) throws RazorpayException {
      JSONObject captureRequest = new JSONObject();
      captureRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
      captureRequest.put("currency", "INR");
      com.razorpay.Payment payment = razorpayClient.payments.capture(paymentId, captureRequest);
      return convertPaymentToMap(payment);
  }
  
  // Helper method to convert Razorpay Order to Map
  private Map<String, Object> convertOrderToMap(com.razorpay.Order razorpayOrder) {
      Map<String, Object> orderMap = new HashMap<>();
      
      // Extract common fields from Razorpay Order
      orderMap.put("id", razorpayOrder.get("id"));
      orderMap.put("amount", razorpayOrder.get("amount"));
      orderMap.put("currency", razorpayOrder.get("currency"));
      orderMap.put("receipt", razorpayOrder.get("receipt"));
      orderMap.put("status", razorpayOrder.get("status"));
      orderMap.put("attempts", razorpayOrder.get("attempts"));
      orderMap.put("created_at", razorpayOrder.get("created_at"));
      
      return orderMap;
  }
  
  // Helper method to convert Razorpay Payment to Map
  private Map<String, Object> convertPaymentToMap(com.razorpay.Payment razorpayPayment) {
      Map<String, Object> paymentMap = new HashMap<>();
      
      // Extract common fields from Razorpay Payment
      paymentMap.put("id", razorpayPayment.get("id"));
      paymentMap.put("amount", razorpayPayment.get("amount"));
      paymentMap.put("currency", razorpayPayment.get("currency"));
      paymentMap.put("status", razorpayPayment.get("status"));
      paymentMap.put("method", razorpayPayment.get("method"));
      paymentMap.put("order_id", razorpayPayment.get("order_id"));
      paymentMap.put("created_at", razorpayPayment.get("created_at"));
      paymentMap.put("captured", razorpayPayment.get("captured"));
      
      return paymentMap;
  }

  private String generateOrderId() {
      return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + 
             "-" + System.currentTimeMillis();
  }

  private String generatePaymentId() {
      return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + 
             "-" + System.currentTimeMillis();
  }

  private String generateSignature(String orderId, String paymentId, String secret) {
      String data = orderId + "|" + paymentId;
      try {
          javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
          javax.crypto.spec.SecretKeySpec secretKeySpec = 
                  new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256");
          mac.init(secretKeySpec);
          byte[] signature = mac.doFinal(data.getBytes());
          return bytesToHex(signature);
      } catch (Exception e) {
          throw new RuntimeException("Error generating signature", e);
      }
  }

  private String bytesToHex(byte[] bytes) {
      StringBuilder result = new StringBuilder();
      for (byte b : bytes) {
          result.append(String.format("%02x", b));
      }
      return result.toString();
  }
  
  // Additional utility methods
  
  public Map<String, Object> getPaymentDetails(String paymentId) throws RazorpayException {
      com.razorpay.Payment payment = razorpayClient.payments.fetch(paymentId);
      return convertPaymentToMap(payment);
  }
  
  public Map<String, Object> refundPayment(String paymentId, BigDecimal amount) throws RazorpayException {
      JSONObject refundRequest = new JSONObject();
      refundRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
      
      com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);
      
      Map<String, Object> refundMap = new HashMap<>();
      refundMap.put("id", refund.get("id"));
      refundMap.put("amount", refund.get("amount"));
      refundMap.put("currency", refund.get("currency"));
      refundMap.put("payment_id", refund.get("payment_id"));
      refundMap.put("status", refund.get("status"));
      
      return refundMap;
  }
}
