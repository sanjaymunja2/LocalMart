package com.localmart.backend.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String category;
    private Double price;
    private Integer quantity;
    private String imageUrl;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name, String category, Double price, Integer quantity) {
        this(id, name, category, price, quantity, null);
    }

    public ProductResponse(Long id, String name, String category, Double price, Integer quantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
