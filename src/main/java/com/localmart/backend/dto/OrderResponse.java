package com.localmart.backend.dto;

import com.localmart.backend.entity.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        List<OrderItemResponse> orderItems,
        Integer totalItems,
        Double totalAmount,
        Instant createdAt
) {}
