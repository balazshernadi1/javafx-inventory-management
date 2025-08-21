package com.balazsh.inventory.features.dashboard.product;

import com.balazsh.inventory.domain.model.ProductEntry;
import com.balazsh.inventory.domain.model.ProductPurchase;
import com.balazsh.inventory.domain.model.ProductSale;
import com.balazsh.inventory.domain.model.Result;
import javafx.scene.control.Alert;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Product business logic handling inventory operations, validation, and transaction processing.
 * Manages product selection validation, quantity calculations, and transaction creation.
 */
public class ProductInteractor {

    private final ProductModel productModel;

    public ProductInteractor(ProductModel productModel) {
        this.productModel = productModel;
    }

    /** Validates that selected products have sufficient stock for sale operations */
    public boolean prePopulateSalesQuantitiesValidation(){
        return productModel.getProductEntries()
                .stream()
                .anyMatch(productEntry -> productEntry.isSelected() && productEntry.getInStock() > 0);
    }

    /** Sets the first selected product as the active product for transaction operations */
    public void addActiveProductEntry(){
        productModel.getProductEntries()
                .stream()
                .filter(ProductEntry::isSelected)
                .findFirst()
                .ifPresent(
                        activeEntry -> productModel
                                .activeProductEntryObjectPropertyProperty()
                                .set(activeEntry));
    }

    /** Populates available quantities (1 to stock count) for sale quantity selection */
    public void populateSaleQuantitiesSelection(){
        productModel.getQuantities().clear();
        List<Integer> quantities = productModel.getProductEntries()
                .stream()
                .filter(ProductEntry::isSelected)
                .flatMap((productEntry)-> IntStream.rangeClosed(1, productEntry.getInStock()).boxed()).toList();
        productModel.getQuantities().addAll(quantities);
    }

    /** Validates that exactly one product is selected for operations */
    public boolean isSingleProductSelected() {
        long count = productModel.getProductEntries().stream()
                .filter(ProductEntry::isSelected)
                .limit(2)  // Only check up to 2 for efficiency
                .count();

        return count == 1;
    }

    /** Creates product sale transaction object from form data */
    public void createProductSale(){
        ProductEntry productEntry = productModel.getActiveProductEntryObjectProperty();
        int quantity = productModel.getQuantity();
        int price = Integer.parseInt(productModel.getPrice());
        productModel.productSaleObjectPropertyProperty().set(new ProductSale(productEntry.getId(), price, quantity));
    }

    /** Creates product purchase transaction object from form data */
    public void createProductPurchase(){
        ProductEntry productEntry = productModel.getActiveProductEntryObjectProperty();
        int quantity = Integer.parseInt(productModel.getBuyQuantity());
        productModel.productPurchaseObjectPropertyProperty().set(new ProductPurchase(productEntry.getId(), quantity));
    }

    /** Validates that price input is a valid number */
    public boolean preSaleValidation(){
        boolean isValid = true;
        try {
            Integer.parseInt(productModel.getPrice());
        } catch (NumberFormatException e){
            isValid = false;
        }
        return isValid;
    }

    /** Displays operation result as success or error alert dialog */
    public void resolveResult(){
        Result result = productModel.getResultObjectProperty();
        Alert alert;
        if (result.getStatus().equals("success")){
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
        }
        alert.setContentText(result.getMessage());
        alert.show();
    }

    /** 
     * Validates purchase quantity is a valid number within business limits.
     * Enforces maximum quantity of 100 and minimum of 1.
     */
    public boolean prePurchaseValidation(){
        boolean isValid = true;
        try {
            int quantity = Integer.parseInt(productModel.getBuyQuantity());
            if (quantity > 100 || quantity <= 0){  // Business rule: max 100, min 1
                isValid = false;
            }
        } catch (NumberFormatException e){
            isValid = false;
        }

        return isValid;
    }
}
