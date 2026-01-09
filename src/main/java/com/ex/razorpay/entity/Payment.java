//package com.ex.razorpay.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import com.ex.razorpay.enumm.PaymentStatus;
//
//@Entity
//@Table(name = "payments")
//@Data
//public class Payment {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    @Column(name = "payment_id", unique = true)
//    private String paymentId;
//    
//    @Column(name = "razorpay_payment_id")
//    private String razorpayPaymentId;
//    
//    @Column(name = "razorpay_order_id")
//    private String razorpayOrderId;
//    
//    @Column(name = "razorpay_signature")
//    private String razorpaySignature;
//    
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal amount;
//    
//    @Column(nullable = false)
//    private String currency;
//    
//    @Enumerated(EnumType.STRING)
//    private PaymentStatus status;
//    
//    @Column(name = "payment_method")
//    private String paymentMethod;
//    
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//    
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;
//    
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        if (status == null) {
//            status = PaymentStatus.PENDING;
//        }
//        if (currency == null) {
//            currency = "INR";
//        }
//    }
//}
//
//package com.ex.razorpay.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import com.ex.razorpay.enumm.PaymentStatus;
//
//@Entity
//@Table(name = "payments")
//@Data
//public class Payment {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    @Column(name = "payment_id", unique = true)
//    private String paymentId;
//    
//    @Column(name = "razorpay_payment_id")
//    private String razorpayPaymentId;
//    
//    @Column(name = "razorpay_order_id")
//    private String razorpayOrderId;
//    
//    @Column(name = "razorpay_signature")
//    private String razorpaySignature;
//    
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal amount;
//    
//    @Column(nullable = false)
//    private String currency;
//    
//    @Enumerated(EnumType.STRING)
//    private PaymentStatus status;
//    
//    @Column(name = "payment_method")
//    private String paymentMethod;
//    
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//    
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;
//    
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        if (status == null) {
//            status = PaymentStatus.PENDING;
//        }
//        if (currency == null) {
//            currency = "INR";
//        }
//    }
//    
////    public enum PaymentStatus {
////        PENDING, SUCCESS, FAILED, REFUNDED
////    }
//}


package com.ex.razorpay.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.ex.razorpay.enumm.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payment_id", unique = true)
    private String paymentId;
    
    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;
    
    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;
    
    @Column(name = "razorpay_signature")
    private String razorpaySignature;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String currency;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
        if (currency == null) {
            currency = "INR";
        }
    }
}