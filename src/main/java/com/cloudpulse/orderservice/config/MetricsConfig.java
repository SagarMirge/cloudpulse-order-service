package com.cloudpulse.orderservice.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers custom business-level metrics on top of the default JVM/HTTP
 * metrics that Micrometer + Actuator expose automatically.
 *
 * This is deliberately separate from generic infra metrics (heap, GC, request
 * count) because interviewers specifically look for candidates who understand
 * *what* is worth monitoring in a domain, not just how to wire up a metrics
 * library.
 */
@Configuration
public class MetricsConfig {

    @Bean
    public Counter ordersCreatedCounter(MeterRegistry registry) {
        return Counter.builder("orders.created.total")
                .description("Total number of orders successfully created")
                .register(registry);
    }

    @Bean
    public Counter ordersFailedCounter(MeterRegistry registry) {
        return Counter.builder("orders.failed.total")
                .description("Total number of order creation attempts that failed validation or processing")
                .register(registry);
    }

    @Bean
    public Counter ordersCancelledCounter(MeterRegistry registry) {
        return Counter.builder("orders.cancelled.total")
                .description("Total number of orders cancelled after creation")
                .register(registry);
    }

    @Bean
    public Timer orderProcessingTimer(MeterRegistry registry) {
        return Timer.builder("orders.processing.duration")
                .description("Time taken to process an order from creation to confirmation")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }
}
