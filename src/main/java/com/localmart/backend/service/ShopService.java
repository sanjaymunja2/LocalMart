package com.localmart.backend.service;

import com.localmart.backend.dto.ShopRequest;
import com.localmart.backend.dto.ShopResponse;
import com.localmart.backend.entity.Shop;
import com.localmart.backend.repository.ShopRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ShopService {

    private static final long SHOP_ID = 1L;
    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public ShopResponse getShop() {
        return shopRepository.findById(SHOP_ID)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop information has not been configured"));
    }

    @Transactional
    public ShopResponse updateShop(ShopRequest request) {
        Shop shop = shopRepository.findById(SHOP_ID).orElseGet(() -> {
            Shop created = new Shop();
            created.setId(SHOP_ID);
            return created;
        });
        shop.setName(request.name());
        shop.setLogoUrl(request.logoUrl());
        shop.setBannerUrl(request.bannerUrl());
        shop.setOwnerName(request.ownerName());
        shop.setPhoneNumber(request.phoneNumber());
        shop.setWhatsappNumber(request.whatsappNumber());
        shop.setEmail(request.email());
        shop.setAddress(request.address());
        shop.setGoogleMapsEmbedUrl(request.googleMapsEmbedUrl());
        shop.setWorkingHours(request.workingHours());
        shop.setDescription(request.description());
        return toResponse(shopRepository.save(shop));
    }

    private ShopResponse toResponse(Shop shop) {
        return new ShopResponse(shop.getName(), shop.getLogoUrl(), shop.getBannerUrl(), shop.getOwnerName(),
                shop.getPhoneNumber(), shop.getWhatsappNumber(), shop.getEmail(), shop.getAddress(),
                shop.getGoogleMapsEmbedUrl(), shop.getWorkingHours(), shop.getDescription());
    }
}
