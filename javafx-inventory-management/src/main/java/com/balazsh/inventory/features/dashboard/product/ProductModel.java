package com.balazsh.inventory.features.dashboard.product;

import com.balazsh.inventory.domain.model.ProductEntry;
import com.balazsh.inventory.domain.model.ProductPurchase;
import com.balazsh.inventory.domain.model.ProductSale;
import com.balazsh.inventory.domain.model.Result;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductModel {

    private final ObservableList<ProductEntry> productEntries = FXCollections.observableArrayList(); // Product inventory list
    private final ObservableList<Integer> quantities = FXCollections.observableArrayList();  // Available quantities for forms
    private final ObjectProperty<ProductEntry> activeProductEntryObjectProperty = new SimpleObjectProperty<>(); // Currently selected product
    private final ObjectProperty<ProductSale> productSaleObjectProperty = new SimpleObjectProperty<>(); // Sale transaction data
    private final ObjectProperty<ProductPurchase> productPurchaseObjectProperty = new SimpleObjectProperty<>(); // Purchase transaction data
    private final BooleanProperty showSaleForm = new SimpleBooleanProperty(false); // Sale form modal visibility
    private final BooleanProperty showBuyForm = new SimpleBooleanProperty(false); // Purchase form modal visibility
    private final SimpleStringProperty price = new SimpleStringProperty(); // Price input for transactions
    private final IntegerProperty quantity = new SimpleIntegerProperty(); // Quantity input for transactions
    private final SimpleStringProperty buyQuantity = new SimpleStringProperty(); // Purchase quantity input
    private final BooleanProperty quantityError = new SimpleBooleanProperty(false); // Quantity validation error
    private final BooleanProperty priceError = new SimpleBooleanProperty(false); // Price validation error
    private final ObjectProperty<Result> resultObjectProperty = new SimpleObjectProperty<>(); // Operation results
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false); // Loading state
    private final DoubleProperty progress = new SimpleDoubleProperty(); // Progress tracking

    public String getBuyQuantity() {
        return buyQuantity.get();
    }

    public SimpleStringProperty buyQuantityProperty() {
        return buyQuantity;
    }

    public ProductEntry getActiveProductEntryObjectProperty() {
        return activeProductEntryObjectProperty.get();
    }

    public ObjectProperty<ProductEntry> activeProductEntryObjectPropertyProperty() {
        return activeProductEntryObjectProperty;
    }

    public boolean isQuantityError() {
        return quantityError.get();
    }

    public BooleanProperty quantityErrorProperty() {
        return quantityError;
    }

    public boolean isPriceError() {
        return priceError.get();
    }

    public BooleanProperty priceErrorProperty() {
        return priceError;
    }

    public ProductSale getProductSaleObjectProperty() {
        return productSaleObjectProperty.get();
    }

    public ObjectProperty<ProductSale> productSaleObjectPropertyProperty() {
        return productSaleObjectProperty;
    }

    public ProductPurchase getProductPurchaseObjectProperty() {
        return productPurchaseObjectProperty.get();
    }

    public ObjectProperty<ProductPurchase> productPurchaseObjectPropertyProperty() {
        return productPurchaseObjectProperty;
    }

    public boolean isShowSaleForm() {
        return showSaleForm.get();
    }

    public BooleanProperty showSaleFormProperty() {
        return showSaleForm;
    }

    public boolean isShowBuyForm() {
        return showBuyForm.get();
    }

    public BooleanProperty showBuyFormProperty() {
        return showBuyForm;
    }

    public ObservableList<Integer> getQuantities() {
        return quantities;
    }

    public String getPrice() {
        return price.get();
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public boolean isIsLoading() {
        return isLoading.get();
    }

    public BooleanProperty isLoadingProperty() {
        return isLoading;
    }

    public Result getResultObjectProperty() {
        return resultObjectProperty.get();
    }

    public ObjectProperty<Result> resultObjectPropertyProperty() {
        return resultObjectProperty;
    }

    public ObservableList<ProductEntry> getProductEntries() {
        return productEntries;
    }
}
