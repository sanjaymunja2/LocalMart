package com.localmart.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ShopRequest(
        @NotBlank String name,
        String logoUrl,
        String bannerUrl,
        @NotBlank String ownerName,
        @NotBlank String phoneNumber,
        @NotBlank String whatsappNumber,
        @NotBlank @Email String email,
        @NotBlank String address,
        String googleMapsEmbedUrl,
        @NotBlank String workingHours,
        @NotBlank String description
) {}
