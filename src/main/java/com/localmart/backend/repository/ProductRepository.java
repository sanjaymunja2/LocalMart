package com.localmart.backend.repository;
import com.localmart.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> {

}
