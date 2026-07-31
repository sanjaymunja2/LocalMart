package com.localmart.backend.controller;
import jakarta.validation.Valid;
import com.localmart.backend.entity.Product;
import com.localmart.backend.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;
import org.springframework.data.domain.Page;
import com.localmart.backend.dto.ProductRequest;
import com.localmart.backend.dto.ProductResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/products")
    public Page<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        return productService.getAllProducts(page, size, sortBy);
    }
    @PostMapping("/products")
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request) {

        return productService.createProduct(request);
    }
    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @Valid @RequestBody Product product) {

        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/products/{id}")
    public String deleteProduct(@PathVariable Long id) {

        boolean deleted = productService.deleteProduct(id);

        if (deleted) {
            return "Product deleted successfully";
        }

        return "Product not found";
    }
    @GetMapping("/products/search")
    public List<ProductResponse> searchProducts(
            @RequestParam String keyword) {

        return productService.searchProducts(keyword);
    }
    @GetMapping("/products/filter")
    public List<ProductResponse> filterByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {

        return productService.filterByPrice(minPrice, maxPrice);
    }

}