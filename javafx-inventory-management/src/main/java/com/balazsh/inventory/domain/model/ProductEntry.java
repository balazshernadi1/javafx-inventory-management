package com.balazsh.inventory.domain.model;

import javafx.beans.property.*;

public class ProductEntry {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty image = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final IntegerProperty numberOfProductsAvailableForPurchase = new SimpleIntegerProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final IntegerProperty inStock = new SimpleIntegerProperty();

    public ProductEntry() {
        // Default constructor
    }

    public ProductEntry(int id, String image, String name, String category, int inStock, int availableForPurchase) {
        this.id.set(id);
        this.image.set(image);
        this.name.set(name);
        this.category.set(category);
        this.inStock.set(inStock);
        this.numberOfProductsAvailableForPurchase.set(availableForPurchase);
        this.selected.set(false);
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public void setInStock(int inStock) {
        this.inStock.set(inStock);
    }

    public void setNumberOfProductsAvailableForPurchase(int numberOfProductsAvailableForPurchase) {
        this.numberOfProductsAvailableForPurchase.set(numberOfProductsAvailableForPurchase);
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    // Existing getters and property methods
    public int getInStock() {
        return inStock.get();
    }

    public IntegerProperty inStockProperty() {
        return inStock;
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getImage() {
        return image.get();
    }

    public StringProperty imageProperty() {
        return image;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getNumberOfProductsAvailableForPurchase() {
        return numberOfProductsAvailableForPurchase.get();
    }

    public IntegerProperty numberOfProductsAvailableForPurchaseProperty() {
        return numberOfProductsAvailableForPurchase;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }
}
