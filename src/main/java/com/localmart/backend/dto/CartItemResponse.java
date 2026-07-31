package com.localmart.backend.dto;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        Double unitPrice,
        Integer quantity,
        Double subtotal
) {}
