package com.cloudpulse.orderservice.service;

import com.cloudpulse.orderservice.exception.OrderNotFoundException;
import com.cloudpulse.orderservice.model.Order;
import com.cloudpulse.orderservice.model.OrderStatus;
import com.cloudpulse.orderservice.repository.OrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final Counter ordersCreatedCounter;
    private final Counter ordersFailedCounter;
    private final Counter ordersCancelledCounter;
    private final Timer orderProcessingTimer;

    public OrderService(OrderRepository orderRepository,
                         Counter ordersCreatedCounter,
                         Counter ordersFailedCounter,
                         Counter ordersCancelledCounter,
                         Timer orderProcessingTimer) {
        this.orderRepository = orderRepository;
        this.ordersCreatedCounter = ordersCreatedCounter;
        this.ordersFailedCounter = ordersFailedCounter;
        this.ordersCancelledCounter = ordersCancelledCounter;
        this.orderProcessingTimer = orderProcessingTimer;
    }

    public Order createOrder(Order order) {
        long start = System.nanoTime();
        try {
            order.setTotalAmount(computeTotal(order));
            Order saved = orderRepository.save(order);
            ordersCreatedCounter.increment();
            log.info("Order created: id={}, customer={}, product={}",
                    saved.getId(), saved.getCustomerName(), saved.getProductName());
            return saved;
        } catch (Exception ex) {
            ordersFailedCounter.increment();
            log.error("Order creation failed for customer={}: {}", order.getCustomerName(), ex.getMessage());
            throw ex;
        } finally {
            orderProcessingTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Order updateStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        if (status == OrderStatus.CANCELLED) {
            ordersCancelledCounter.increment();
        }
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

    private Double computeTotal(Order order) {
        // Placeholder pricing logic — in a real system this would call a
        // pricing/inventory service. Kept simple and self-contained here
        // deliberately, so the project stays runnable without external deps.
        double unitPrice = 100.0;
        return unitPrice * order.getQuantity();
    }
}
