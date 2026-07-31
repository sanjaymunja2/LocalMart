package com.localmart.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shop")
public class Shop {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;
    private String logoUrl;
    private String bannerUrl;
    @Column(nullable = false)
    private String ownerName;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String whatsappNumber;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false, length = 1000)
    private String address;
    @Column(length = 2000)
    private String googleMapsEmbedUrl;
    @Column(nullable = false, length = 500)
    private String workingHours;
    @Column(nullable = false, length = 4000)
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getWhatsappNumber() { return whatsappNumber; }
    public void setWhatsappNumber(String whatsappNumber) { this.whatsappNumber = whatsappNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getGoogleMapsEmbedUrl() { return googleMapsEmbedUrl; }
    public void setGoogleMapsEmbedUrl(String googleMapsEmbedUrl) { this.googleMapsEmbedUrl = googleMapsEmbedUrl; }
    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
