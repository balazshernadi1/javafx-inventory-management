package com.balazsh.inventory.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Component {

    @JsonProperty("product_id")
    private Object productId;

    @JsonProperty("display_name")
    private String displayName;

    public Component() {
    }

    public Component(int productId, String displayName) {
        this.productId = productId;
        this.displayName = displayName;
    }

    public int getProductId() {
        return (int) productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
