package com.cloudpulse.orderservice.repository;

import com.cloudpulse.orderservice.model.Order;
import com.cloudpulse.orderservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerNameIgnoreCase(String customerName);
}
