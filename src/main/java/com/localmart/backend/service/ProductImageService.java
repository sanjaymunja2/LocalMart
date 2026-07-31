package com.localmart.backend.service;

import com.localmart.backend.entity.Product;
import com.localmart.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductImageService {

    private static final Map<String, String> CONTENT_TYPES_BY_EXTENSION = Map.of(
            "jpg", MediaType.IMAGE_JPEG_VALUE,
            "jpeg", MediaType.IMAGE_JPEG_VALUE,
            "png", MediaType.IMAGE_PNG_VALUE,
            "webp", "image/webp"
    );

    private final ProductRepository productRepository;
    private final Path storageDirectory;
    private final long maxImageSize;

    public ProductImageService(ProductRepository productRepository,
                               @Value("${app.upload.product-directory:uploads/products}") String productDirectory,
                               @Value("${app.upload.max-image-size:5242880}") long maxImageSize) {
        this.productRepository = productRepository;
        this.storageDirectory = Path.of(productDirectory).toAbsolutePath().normalize();
        this.maxImageSize = maxImageSize;
    }

    @Transactional
    public Product upload(Long productId, MultipartFile image) {
        validateImage(image);
        Product product = findProduct(productId);
        String extension = extractExtension(image.getOriginalFilename());
        Path destination = storageDirectory.resolve(UUID.randomUUID() + "." + extension).normalize();
        if (!destination.startsWith(storageDirectory)) {
            throw new IllegalArgumentException("Invalid image filename");
        }

        try {
            Files.createDirectories(storageDirectory);
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to store product image", ex);
        }

        String previousPath = product.getImagePath();
        product.setImagePath(destination.toString());
        Product savedProduct = productRepository.save(product);
        deletePreviousImage(previousPath, destination);
        return savedProduct;
    }

    public ImageResource getImage(Long productId) {
        Product product = findProduct(productId);
        if (product.getImagePath() == null) {
            throw new ImageNotFoundException();
        }

        Path imagePath = Path.of(product.getImagePath()).toAbsolutePath().normalize();
        if (!imagePath.startsWith(storageDirectory) || !Files.isRegularFile(imagePath)) {
            throw new ImageNotFoundException();
        }
        String extension = extractExtension(imagePath.getFileName().toString());
        return new ImageResource(new FileSystemResource(imagePath), MediaType.parseMediaType(CONTENT_TYPES_BY_EXTENSION.get(extension)));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ImageNotFoundException());
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        if (image.getSize() > maxImageSize) {
            throw new IllegalArgumentException("Image size must not exceed 5 MB");
        }

        String extension = extractExtension(image.getOriginalFilename());
        String expectedContentType = CONTENT_TYPES_BY_EXTENSION.get(extension);
        if (expectedContentType == null || !expectedContentType.equalsIgnoreCase(image.getContentType())) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG, and WEBP images are allowed");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Image filename is required");
        }
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex < 1 || extensionIndex == filename.length() - 1) {
            throw new IllegalArgumentException("Image file must have a valid extension");
        }
        return filename.substring(extensionIndex + 1).toLowerCase(Locale.ROOT);
    }

    private void deletePreviousImage(String previousPath, Path newPath) {
        if (previousPath == null) {
            return;
        }
        try {
            Path previous = Path.of(previousPath).toAbsolutePath().normalize();
            if (previous.startsWith(storageDirectory) && !previous.equals(newPath)) {
                Files.deleteIfExists(previous);
            }
        } catch (IOException ignored) {
            // The replacement remains valid; a stale file can be safely cleaned up later.
        }
    }

    public record ImageResource(Resource resource, MediaType mediaType) {}
}
