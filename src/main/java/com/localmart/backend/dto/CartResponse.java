package com.localmart.backend.dto;

import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> cartItems,
        Integer totalItems,
        Double subtotal
) {}
