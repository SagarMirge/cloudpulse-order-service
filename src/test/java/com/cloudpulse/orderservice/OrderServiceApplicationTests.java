package com.cloudpulse.orderservice;

import com.cloudpulse.orderservice.model.Order;
import com.cloudpulse.orderservice.repository.OrderRepository;
import com.cloudpulse.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceApplicationTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void contextLoads() {
        assertThat(orderService).isNotNull();
    }

    @Test
    void createOrder_persistsAndComputesTotal() {
        Order order = new Order();
        order.setCustomerName("Test Customer");
        order.setProductName("Test Product");
        order.setQuantity(3);

        Order saved = orderService.createOrder(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTotalAmount()).isEqualTo(300.0);
        assertThat(orderRepository.findById(saved.getId())).isPresent();
    }
}
