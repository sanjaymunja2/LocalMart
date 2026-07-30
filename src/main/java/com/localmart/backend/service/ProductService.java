package com.localmart.backend.service;
import org.springframework.stereotype.Service;
import com.localmart.backend.repository.ProductRepository;
import com.localmart.backend.entity.Product;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

}
