package com.localmart.backend.dto;

public record ShopResponse(
        String name,
        String logoUrl,
        String bannerUrl,
        String ownerName,
        String phoneNumber,
        String whatsappNumber,
        String email,
        String address,
        String googleMapsEmbedUrl,
        String workingHours,
        String description
) {}
