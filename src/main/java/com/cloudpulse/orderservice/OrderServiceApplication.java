package com.cloudpulse.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CloudPulse Order Service
 *
 * A production-style order management microservice built to demonstrate
 * a full cloud-native delivery pipeline: containerized with Docker,
 * deployed to AWS EKS (and, as an alternate path, Azure AKS), and
 * instrumented end-to-end with Prometheus + Grafana via Micrometer.
 */
@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
