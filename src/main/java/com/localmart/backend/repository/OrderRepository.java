package com.localmart.backend.repository;

import com.localmart.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select distinct o from CustomerOrder o join fetch o.user left join fetch o.orderItems items left join fetch items.product where o.user.id = :userId order by o.createdAt desc")
    List<Order> findAllWithItemsByUserId(Long userId);

    @Query("select distinct o from CustomerOrder o join fetch o.user left join fetch o.orderItems items left join fetch items.product where o.id = :orderId")
    Optional<Order> findWithItemsById(Long orderId);

    @Query("select distinct o from CustomerOrder o join fetch o.user left join fetch o.orderItems items left join fetch items.product order by o.createdAt desc")
    List<Order> findAllWithItems();
}
