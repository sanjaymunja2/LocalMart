package com.localmart.backend.controller;
import jakarta.validation.Valid;
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
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return productService.getAllProducts(page, size, sortBy, direction);
    }
    @PostMapping("/products")
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request) {

        return productService.createProduct(request);
    }


    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return productService.updateProduct(id, request);
    }
    @GetMapping("/products/search")
    public List<ProductResponse> searchProducts(
            @RequestParam String keyword) {

        return productService.searchProducts(keyword);
    }
    @GetMapping("/products/filter")
    public List<ProductResponse> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword) {

        return productService.filterProducts(category, minPrice, maxPrice, keyword);
    }

    @GetMapping("/products/category")
    public List<ProductResponse> searchProductsByCategory(@RequestParam String category) {
        return productService.searchProductsByCategory(category);
    }

}
