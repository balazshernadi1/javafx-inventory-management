package com.balazsh.inventory.features.dashboard.product;

import com.balazsh.inventory.domain.model.ProductEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductInteractorTest {

    private ProductModel productModel;
    private ProductInteractor productInteractor;

    @BeforeEach
    void setUp() {
        productModel = new ProductModel();
        productInteractor = new ProductInteractor(productModel);
    }

    @Test
    void prePopulateSalesQuantitiesValidation_ShouldReturnTrue_WhenSelectedProductHasStock() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 0, 5);
        
        product1.setSelected(true);  // Selected and has stock
        product2.setSelected(false);
        
        productModel.getProductEntries().addAll(product1, product2);

        // When
        boolean result = productInteractor.prePopulateSalesQuantitiesValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void prePopulateSalesQuantitiesValidation_ShouldReturnFalse_WhenSelectedProductHasNoStock() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 0, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 5, 5);
        
        product1.setSelected(true);  // Selected but no stock
        product2.setSelected(false);
        
        productModel.getProductEntries().addAll(product1, product2);

        // When
        boolean result = productInteractor.prePopulateSalesQuantitiesValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void prePopulateSalesQuantitiesValidation_ShouldReturnFalse_WhenNoProductSelected() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 3, 5);
        
        product1.setSelected(false);
        product2.setSelected(false);
        
        productModel.getProductEntries().addAll(product1, product2);

        // When
        boolean result = productInteractor.prePopulateSalesQuantitiesValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void isSingleProductSelected_ShouldReturnTrue_WhenExactlyOneProductIsSelected() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 3, 5);
        ProductEntry product3 = new ProductEntry(3, "image3", "Product3", "Category3", 8, 15);
        
        product1.setSelected(false);
        product2.setSelected(true);  // Only this one is selected
        product3.setSelected(false);
        
        productModel.getProductEntries().addAll(product1, product2, product3);

        // When
        boolean result = productInteractor.isSingleProductSelected();

        // Then
        assertTrue(result);
    }

    @Test
    void isSingleProductSelected_ShouldReturnFalse_WhenNoProductIsSelected() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 3, 5);
        
        product1.setSelected(false);
        product2.setSelected(false);
        
        productModel.getProductEntries().addAll(product1, product2);

        // When
        boolean result = productInteractor.isSingleProductSelected();

        // Then
        assertFalse(result);
    }

    @Test
    void isSingleProductSelected_ShouldReturnFalse_WhenMultipleProductsAreSelected() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 3, 5);
        
        product1.setSelected(true);
        product2.setSelected(true);  // Two products selected
        
        productModel.getProductEntries().addAll(product1, product2);

        // When
        boolean result = productInteractor.isSingleProductSelected();

        // Then
        assertFalse(result);
    }

    @Test
    void preSaleValidation_ShouldReturnTrue_WhenPriceIsValidNumber() {
        // Given
        productModel.priceProperty().set("100");

        // When
        boolean result = productInteractor.preSaleValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void preSaleValidation_ShouldReturnTrue_WhenPriceIsZero() {
        // Given
        productModel.priceProperty().set("0");

        // When
        boolean result = productInteractor.preSaleValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void preSaleValidation_ShouldReturnFalse_WhenPriceIsNotANumber() {
        // Given
        productModel.priceProperty().set("not a number");

        // When
        boolean result = productInteractor.preSaleValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void preSaleValidation_ShouldReturnFalse_WhenPriceIsEmpty() {
        // Given
        productModel.priceProperty().set("");

        // When
        boolean result = productInteractor.preSaleValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void prePurchaseValidation_ShouldReturnTrue_WhenQuantityIsValidAndWithinLimits() {
        // Given
        productModel.buyQuantityProperty().set("50");

        // When
        boolean result = productInteractor.prePurchaseValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void prePurchaseValidation_ShouldReturnTrue_WhenQuantityIsOne() {
        // Given
        productModel.buyQuantityProperty().set("1");

        // When
        boolean result = productInteractor.prePurchaseValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void prePurchaseValidation_ShouldReturnTrue_WhenQuantityIsMaximum() {
        // Given
        productModel.buyQuantityProperty().set("100");

        // When
        boolean result = productInteractor.prePurchaseValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void prePurchaseValidation_ShouldReturnFalse_WhenQuantityIsZero() {
        // Given
        productModel.buyQuantityProperty().set("0");

        // When
        boolean result = productInteractor.prePurchaseValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void prePurchaseValidation_ShouldReturnFalse_WhenQuantityIsNegative() {
        // Given
        productModel.buyQuantityProperty().set("-5");

        // When
        boolean result = productInteractor.prePurchaseValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void prePurchaseValidation_ShouldReturnFalse_WhenQuantityExceedsMaximum() {
        // Given
        productModel.buyQuantityProperty().set("101");

        // When
        boolean result = productInteractor.prePurchaseValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void prePurchaseValidation_ShouldReturnFalse_WhenQuantityIsNotANumber() {
        // Given
        productModel.buyQuantityProperty().set("not a number");

        // When
        boolean result = productInteractor.prePurchaseValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void prePurchaseValidation_ShouldReturnFalse_WhenQuantityIsEmpty() {
        // Given
        productModel.buyQuantityProperty().set("");

        // When
        boolean result = productInteractor.prePurchaseValidation();

        // Then
        assertFalse(result);
    }
}