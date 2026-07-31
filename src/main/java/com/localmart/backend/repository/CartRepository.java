package com.localmart.backend.repository;

import com.localmart.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select distinct c from Cart c left join fetch c.cartItems items left join fetch items.product where c.user.id = :userId")
    Optional<Cart> findWithItemsByUserId(Long userId);
}
