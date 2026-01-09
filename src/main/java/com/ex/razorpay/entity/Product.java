//package com.ex.razorpay.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import java.math.BigDecimal;
//
//@Entity
//@Table(name = "products")
//@Data
//public class Product {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    @Column(nullable = false)
//    private String name;
//    
//    @Column(length = 1000)
//    private String description;
//    
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal price;
//    
//    @Column(nullable = false)
//    private Integer stock;
//    
//    private String category;
//    
//    private String imageUrl;
//}

package com.ex.razorpay.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock;
    
    private String category;
    
    private String imageUrl;
}