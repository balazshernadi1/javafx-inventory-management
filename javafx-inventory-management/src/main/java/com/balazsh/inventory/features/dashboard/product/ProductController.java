package com.balazsh.inventory.features.dashboard.product;

import com.balazsh.inventory.domain.model.ProductEntry;
import com.balazsh.inventory.domain.model.ProductPurchase;
import com.balazsh.inventory.domain.model.ProductSale;
import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.entity.Product;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.beans.binding.Bindings;

import java.util.function.Consumer;

/**
 * Product controller managing inventory operations and transaction workflows.
 * Handles product selection, sale/purchase forms, printing, and validation.
 */
public class ProductController {

    private final ProductInteractor productInteractor; // Business logic for product operations
    private final ProductModel productModel; // Product state and form data
    private final ProductViewBuilder productViewBuilder; // UI construction and interaction
    
    // Async operation callbacks to parent dashboard controller
    private final Consumer<Runnable> printDetailsAsync; // Print selected product details
    private final Consumer<Runnable> printStockAsync; // Print stock report
    private final Consumer<Runnable> sellProductAsync; // Process product sale
    private final Consumer<Runnable> buyProductAsync; // Process product purchase

    /**
     * Creates product controller with shared state binding and operation callbacks.
     * Sets up bidirectional data binding with parent dashboard for data synchronization.
     */
    public ProductController(ObservableList<ProductEntry> productEntries,
                             ObjectProperty<Result> resultObjectProperty,
                             DoubleProperty progressProperty,
                             ObjectProperty<ProductPurchase> productPurchaseObjectProperty,
                             ObjectProperty<ProductSale> productSaleObjectProperty,
                             Consumer<Runnable> printDetailsAsync,
                             Consumer<Runnable> printStockAsync,
                             Consumer<Runnable> sellProductAsync,
                             Consumer<Runnable> buyProductAsync,
                             Consumer<Runnable> refreshProductAsync) {
        productModel = new ProductModel();
        productInteractor = new ProductInteractor(productModel);
        productViewBuilder = new ProductViewBuilder(
                productModel,
                this::printProductDetails,
                this::printStockDetails,
                this::showSaleForm,
                this::sellProduct,
                this::showBuyForm,
                this::buyProduct,
                refreshProductAsync);

        // Establish bidirectional binding with parent dashboard state
        Bindings.bindContentBidirectional(productModel.getProductEntries(), productEntries);
        productModel.resultObjectPropertyProperty().bind(resultObjectProperty);
        productModel.progressProperty().bind(progressProperty);
        productModel.productPurchaseObjectPropertyProperty().bindBidirectional(productPurchaseObjectProperty);
        productModel.productSaleObjectPropertyProperty().bindBidirectional(productSaleObjectProperty);

        this.printDetailsAsync = printDetailsAsync;
        this.printStockAsync = printStockAsync;
        this.sellProductAsync = sellProductAsync;
        this.buyProductAsync = buyProductAsync;
    }

    /** Shows purchase form modal with validation for single product selection */
    private void showBuyForm(Runnable onBuyComplete) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Product Buy Warning");
        if (!productInteractor.isSingleProductSelected()){
            alert.setContentText("No product selected or more than one product was selected");
            alert.show();
            onBuyComplete.run();
        } else {
            productInteractor.addActiveProductEntry();
            productModel.showBuyFormProperty().set(true);
        }
    }

    /** Shows sale form modal with validation for single product selection and stock availability */
    private void showSaleForm(Runnable onSaleComplete){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Product Sale Warning");
        if (!productInteractor.isSingleProductSelected()){
            alert.setContentText("No product selected or more than one product was selected");
            alert.show();
            onSaleComplete.run();
        } else if (!productInteractor.prePopulateSalesQuantitiesValidation()){
            alert.setContentText("Insufficient stock");
            alert.show();
            onSaleComplete.run();
        } else {
            productInteractor.addActiveProductEntry();
            productInteractor.populateSaleQuantitiesSelection();  // Populate available quantities
            productModel.showSaleFormProperty().set(true);
        }
    }

    /** Processes product purchase with validation and delegates to parent controller */
    private void buyProduct(Runnable onBuyComplete) {
        if (!productInteractor.prePurchaseValidation()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sale warning");
            alert.setContentText("Quantity must be a number and be greater than 0!");
            alert.show();
            onBuyComplete.run();
            return;
        }
        productInteractor.createProductPurchase();
        buyProductAsync.accept(()->{
            productInteractor.resolveResult();
            onBuyComplete.run();
        });
    }

    /** Processes product sale with validation and delegates to parent controller */
    private void sellProduct(Runnable onSaleComplete){
        if (!productInteractor.preSaleValidation()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sale warning");
            alert.setContentText("Price must be a number!");
            alert.show();
            onSaleComplete.run();
            return;
        }
        productInteractor.createProductSale();
        sellProductAsync.accept(()->{
            productInteractor.resolveResult();
            onSaleComplete.run();
        });
    }

    /** Prints details of selected products and processes result */
    private void printProductDetails(Runnable onPrintTaskCompleted) {
        printDetailsAsync.accept(()->{
            productInteractor.resolveResult();
            onPrintTaskCompleted.run();
        });
    }

    /** Prints stock report and processes result */
    private void printStockDetails(Runnable onPrintTaskCompleted) {
        printStockAsync.accept(()->{
            productInteractor.resolveResult();
            onPrintTaskCompleted.run();
        });
    }

    public Region getView() {
        return productViewBuilder.build();
    }
}
