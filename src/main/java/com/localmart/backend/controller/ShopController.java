package com.localmart.backend.controller;

import com.localmart.backend.dto.ShopRequest;
import com.localmart.backend.dto.ShopResponse;
import com.localmart.backend.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public ShopResponse getShop() {
        return shopService.getShop();
    }

    @PutMapping
    public ShopResponse updateShop(@Valid @RequestBody ShopRequest request) {
        return shopService.updateShop(request);
    }
}
