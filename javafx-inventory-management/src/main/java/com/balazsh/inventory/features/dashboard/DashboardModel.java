package com.balazsh.inventory.features.dashboard;

import com.balazsh.inventory.domain.model.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Dashboard model managing application state, user data, and product data.
 * Central hub for data shared between product and user management features.
 */
public class DashboardModel {

    private final BooleanProperty productPageSelected = new SimpleBooleanProperty(); // Product page visibility
    private final BooleanProperty userPageSelected = new SimpleBooleanProperty(); // User page visibility
    private final ObservableList<ProductEntry> productList = FXCollections.observableArrayList();  // Product inventory
    private final ObservableList<UserEntry> userList = FXCollections.observableArrayList(); // User accounts
    private final ObjectProperty<UserEntry> selectedUser = new SimpleObjectProperty<>(); // Selected user for operations
    private final ObjectProperty<ProductSale> productSaleObjectProperty = new SimpleObjectProperty<>(); // Sale transaction data
    private final ObjectProperty<ProductPurchase> productPurchaseObjectProperty = new SimpleObjectProperty<>(); // Purchase transaction data

    private final ObjectProperty<Result> resultObjectProperty = new SimpleObjectProperty<>(new Result("", "")); // Operation results
    private final DoubleProperty progress = new SimpleDoubleProperty(); // Progress tracking
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false); // Loading state
    private final ObjectProperty<ActiveUserDetails> activeUserDetailsObjectProperty = new SimpleObjectProperty<>(); // Current logged-in user

    public UserEntry getSelectedUser() {
        return selectedUser.get();
    }

    public ObjectProperty<UserEntry> selectedUserProperty() {
        return selectedUser;
    }

    public ActiveUserDetails getActiveUserDetailsObjectProperty() {
        return activeUserDetailsObjectProperty.get();
    }

    public ObjectProperty<ActiveUserDetails> activeUserDetailsObjectPropertyProperty() {
        return activeUserDetailsObjectProperty;
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

    public boolean isIsLoading() {
        return isLoading.get();
    }

    public BooleanProperty isLoadingProperty() {
        return isLoading;
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public Result getResultObjectProperty() {
        return resultObjectProperty.get();
    }

    public ObjectProperty<Result> resultObjectPropertyProperty() {
        return resultObjectProperty;
    }

    public ObservableList<ProductEntry> getProductList() {
        return productList;
    }

    public ObservableList<UserEntry> getUserList() {
        return userList;
    }

    public boolean isProductPageSelected() {
        return productPageSelected.get();
    }

    public BooleanProperty productPageSelectedProperty() {
        return productPageSelected;
    }

    public boolean isUserPageSelected() {
        return userPageSelected.get();
    }

    public BooleanProperty userPageSelectedProperty() {
        return userPageSelected;
    }

}
