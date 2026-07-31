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
import com.localmart.backend.service.ProductImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
@RestController
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    public ProductController(ProductService productService, ProductImageService productImageService) {
        this.productService = productService;
        this.productImageService = productImageService;
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
    @ResponseStatus(HttpStatus.CREATED)
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

    @GetMapping("/products/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductResponseById(id);
    }

    @GetMapping("/products/category")
    public List<ProductResponse> searchProductsByCategory(@RequestParam String category) {
        return productService.searchProductsByCategory(category);
    }

    @PostMapping(value = "/products/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse uploadProductImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
        return productService.convertToResponse(productImageService.upload(id, image));
    }

    @GetMapping("/products/{id}/image")
    public ResponseEntity<Resource> getProductImage(@PathVariable Long id) {
        ProductImageService.ImageResource image = productImageService.getImage(id);
        return ResponseEntity.ok().contentType(image.mediaType()).body(image.resource());
    }

}
