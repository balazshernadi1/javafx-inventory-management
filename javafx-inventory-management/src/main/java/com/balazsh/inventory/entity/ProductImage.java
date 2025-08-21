package com.balazsh.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Product_Image", schema = "new")
public class ProductImage {
    @EmbeddedId
    private ProductImageId id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @MapsId("imageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    public ProductImageId getId() {
        return id;
    }

    public void setId(ProductImageId id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}