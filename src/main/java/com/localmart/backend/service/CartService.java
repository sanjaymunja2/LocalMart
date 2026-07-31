package com.localmart.backend.service;

import com.localmart.backend.dto.AddToCartRequest;
import com.localmart.backend.dto.CartItemResponse;
import com.localmart.backend.dto.CartResponse;
import com.localmart.backend.dto.UpdateCartItemRequest;
import com.localmart.backend.entity.Cart;
import com.localmart.backend.entity.CartItem;
import com.localmart.backend.entity.Product;
import com.localmart.backend.entity.User;
import com.localmart.backend.repository.CartItemRepository;
import com.localmart.backend.repository.CartRepository;
import com.localmart.backend.repository.ProductRepository;
import com.localmart.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> createCartItem(cart, product));
        item.setQuantity(item.getQuantity() + request.quantity());
        cartItemRepository.save(item);

        return toResponse(loadCart(user));
    }

    @Transactional
    public CartResponse updateCartItem(Long cartItemId, UpdateCartItemRequest request) {
        User user = getCurrentUser();
        CartItem item = getOwnedCartItem(cartItemId, user);
        item.setQuantity(request.quantity());
        return toResponse(loadCart(user));
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        getOwnedCartItem(cartItemId, getCurrentUser());
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart() {
        Cart cart = getOrCreateCart(getCurrentUser());
        cart.getCartItems().clear();
    }

    @Transactional
    public CartResponse getCart() {
        User user = getCurrentUser();
        return toResponse(getOrCreateCart(user));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findWithItemsByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    private Cart loadCart(User user) {
        return cartRepository.findWithItemsByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Cart could not be loaded"));
    }

    private CartItem createCartItem(Cart cart, Product product) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(0);
        return item;
    }

    private CartItem getOwnedCartItem(Long cartItemId, User user) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have access to this cart item");
        }
        return item;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authentication is required");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists"));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems().stream()
                .map(this::toItemResponse)
                .toList();
        int totalItems = items.stream().mapToInt(CartItemResponse::quantity).sum();
        double subtotal = items.stream().mapToDouble(CartItemResponse::subtotal).sum();
        return new CartResponse(cart.getId(), items, totalItems, subtotal);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        double subtotal = item.getProduct().getPrice() * item.getQuantity();
        return new CartItemResponse(item.getId(), item.getProduct().getId(), item.getProduct().getName(),
                item.getProduct().getPrice(), item.getQuantity(), subtotal);
    }
}
