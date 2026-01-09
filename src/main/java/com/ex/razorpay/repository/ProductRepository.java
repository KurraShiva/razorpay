package com.ex.razorpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ex.razorpay.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
