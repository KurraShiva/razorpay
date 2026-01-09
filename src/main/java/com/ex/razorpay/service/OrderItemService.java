package com.ex.razorpay.service;

import com.ex.razorpay.entity.OrderItem;
import com.ex.razorpay.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    // Get order item by ID
    public Optional<OrderItem> getOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    // Get all order items
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    // Get order items by order ID
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findAll().stream()
                .filter(item -> item.getOrder().getId().equals(orderId))
                .toList();
    }

    // Get order items by product ID
    public List<OrderItem> getOrderItemsByProductId(Long productId) {
        return orderItemRepository.findAll().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .toList();
    }

    // Update order item quantity
    public OrderItem updateOrderItemQuantity(Long id, Integer quantity) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        // Get the difference in quantity
        int quantityDifference = quantity - orderItem.getQuantity();
        
        // Update product stock
        var product = orderItem.getProduct();
        if (quantityDifference > 0) {
            // Adding more items, check stock
            if (product.getStock() < quantityDifference) {
                throw new RuntimeException("Insufficient stock");
            }
            product.setStock(product.getStock() - quantityDifference);
        } else {
            // Reducing items, restore stock
            product.setStock(product.getStock() + Math.abs(quantityDifference));
        }

        orderItem.setQuantity(quantity);
        return orderItemRepository.save(orderItem);
    }

    // Delete order item
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));

        // Restore product stock
        var product = orderItem.getProduct();
        product.setStock(product.getStock() + orderItem.getQuantity());

        orderItemRepository.delete(orderItem);
    }
}