package com.localmart.backend.service;
import com.localmart.backend.dto.ProductRequest;
import com.localmart.backend.dto.ProductResponse;
import com.localmart.backend.entity.Product;
import com.localmart.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Locale;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public boolean deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            return false;
        }

        productRepository.deleteById(id);
        return true;
    }
    public ProductResponse createProduct(ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        Product saved = productRepository.save(product);

        return convertToResponse(saved);
    }
    public List<ProductResponse> getAllProductResponses() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    public Page<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction) {
        validateSortBy(sortBy);
        Sort.Direction sortDirection = parseDirection(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(this::convertToResponse);
    }

    public Page<ProductResponse> getAllProducts(int page, int size, String sortBy) {
        return getAllProducts(page, size, sortBy, "asc");
    }
    public List<ProductResponse> searchProducts(String keyword) {

        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    private ProductResponse convertToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getQuantity()
        );
    }
    public List<ProductResponse> filterByPrice(Double minPrice, Double maxPrice) {

        return productRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<ProductResponse> searchProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<ProductResponse> filterProducts(String category, Double minPrice, Double maxPrice, String keyword) {
        validatePriceRange(minPrice, maxPrice);

        Specification<Product> specification = Specification.allOf(
                hasCategory(category),
                hasMinimumPrice(minPrice),
                hasMaximumPrice(maxPrice),
                hasKeyword(keyword)
        );

        return productRepository.findAll(specification)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private Specification<Product> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> StringUtils.hasText(category)
                ? criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.trim().toLowerCase(Locale.ROOT))
                : null;
    }

    private Specification<Product> hasMinimumPrice(Double minPrice) {
        return (root, query, criteriaBuilder) -> minPrice == null
                ? null
                : criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private Specification<Product> hasMaximumPrice(Double maxPrice) {
        return (root, query, criteriaBuilder) -> maxPrice == null
                ? null
                : criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    private Specification<Product> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> StringUtils.hasText(keyword)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%")
                : null;
    }

    private void validatePriceRange(Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("minPrice must be less than or equal to maxPrice");
        }
    }

    private Sort.Direction parseDirection(String direction) {
        if ("asc".equalsIgnoreCase(direction)) {
            return Sort.Direction.ASC;
        }
        if ("desc".equalsIgnoreCase(direction)) {
            return Sort.Direction.DESC;
        }
        throw new IllegalArgumentException("direction must be either asc or desc");
    }

    private void validateSortBy(String sortBy) {
        if (!List.of("id", "name", "price", "quantity").contains(sortBy)) {
            throw new IllegalArgumentException("sortBy must be one of: name, price, quantity");
        }
    }
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product existingProduct = productRepository.findById(id).orElse(null);

        if (existingProduct == null) {
            return null;
        }

        existingProduct.setName(request.getName());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setQuantity(request.getQuantity());

        Product updated = productRepository.save(existingProduct);

        return convertToResponse(updated);
    }
}
