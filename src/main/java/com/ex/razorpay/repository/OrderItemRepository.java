package com.ex.razorpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.razorpay.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
