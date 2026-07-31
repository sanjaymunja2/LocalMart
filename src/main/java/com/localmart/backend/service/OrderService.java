package com.localmart.backend.service;

import com.localmart.backend.dto.OrderItemResponse;
import com.localmart.backend.dto.OrderResponse;
import com.localmart.backend.entity.Cart;
import com.localmart.backend.entity.CartItem;
import com.localmart.backend.entity.Order;
import com.localmart.backend.entity.OrderItem;
import com.localmart.backend.entity.OrderStatus;
import com.localmart.backend.entity.Product;
import com.localmart.backend.entity.User;
import com.localmart.backend.repository.CartRepository;
import com.localmart.backend.repository.OrderRepository;
import com.localmart.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository,
                        UserRepository userRepository, EntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public OrderResponse placeOrder() {
        User user = getCurrentUser();
        Cart cart = cartRepository.findWithItemsByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(Instant.now());

        double totalAmount = 0;
        List<CartItem> sortedItems = cart.getCartItems().stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getId()))
                .toList();
        for (CartItem cartItem : sortedItems) {
            Product product = entityManager.find(Product.class, cartItem.getProduct().getId(), LockModeType.PESSIMISTIC_WRITE);
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + cartItem.getProduct().getId());
            }
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            OrderItem orderItem = createOrderItem(order, product, cartItem.getQuantity());
            order.getOrderItems().add(orderItem);
            totalAmount += orderItem.getUnitPrice() * orderItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        cart.getCartItems().clear();
        return toResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders() {
        User user = getCurrentUser();
        return orderRepository.findAllWithItemsByUserId(user.getId()).stream().map(this::toResponse).toList();
    }

    public OrderResponse getOrderById(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getUser().getId().equals(user.getId()) && !isAdmin()) {
            throw new AccessDeniedException("You do not have access to this order");
        }
        return toResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Administrator access is required");
        }
        return orderRepository.findAllWithItems().stream().map(this::toResponse).toList();
    }

    private OrderItem createOrderItem(Order order, Product product, Integer quantity) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setUnitPrice(product.getPrice());
        item.setQuantity(quantity);
        return item;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user no longer exists"));
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream().map(this::toItemResponse).toList();
        int totalItems = items.stream().mapToInt(OrderItemResponse::quantity).sum();
        return new OrderResponse(order.getId(), order.getStatus(), items, totalItems,
                order.getTotalAmount(), order.getCreatedAt());
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getId(), item.getProduct().getId(), item.getProductName(),
                item.getUnitPrice(), item.getQuantity(), item.getUnitPrice() * item.getQuantity());
    }
}
