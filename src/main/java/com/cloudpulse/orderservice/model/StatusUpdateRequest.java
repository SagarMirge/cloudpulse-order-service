package com.cloudpulse.orderservice.model;

import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull
    private OrderStatus status;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
