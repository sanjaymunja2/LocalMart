package com.localmart.backend.controller;

import com.localmart.backend.dto.AddToCartRequest;
import com.localmart.backend.dto.CartResponse;
import com.localmart.backend.dto.UpdateCartItemRequest;
import com.localmart.backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public CartResponse addToCart(@Valid @RequestBody AddToCartRequest request) {
        return cartService.addToCart(request);
    }

    @PutMapping("/item/{id}")
    public CartResponse updateCartItem(@PathVariable Long id, @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateCartItem(id, request);
    }

    @DeleteMapping("/item/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(id);
    }

    @DeleteMapping("/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        cartService.clearCart();
    }

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart();
    }
}
