package com.balazsh.inventory.features.dashboard;

import com.balazsh.inventory.domain.model.ProductEntry;
import com.balazsh.inventory.domain.model.UserEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DashboardInteractorTest {

    private DashboardModel dashboardModel;
    private DashboardInteractor dashboardInteractor;

    @BeforeEach
    void setUp() {
        dashboardModel = new DashboardModel();
        dashboardInteractor = new DashboardInteractor(dashboardModel);
    }

    @Test
    void preProductPrintValidation_ShouldReturnTrue_WhenProductListIsEmpty() {
        // Given - empty product list by default

        // When
        boolean result = dashboardInteractor.preProductPrintValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void preProductPrintValidation_ShouldReturnTrue_WhenNoProductsAreSelected() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 3, 5);
        
        product1.setSelected(false);
        product2.setSelected(false);
        
        dashboardModel.getProductList().addAll(product1, product2);

        // When
        boolean result = dashboardInteractor.preProductPrintValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void preProductPrintValidation_ShouldReturnFalse_WhenProductsExistAndSomeAreSelected() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 3, 5);
        
        product1.setSelected(true);  // At least one is selected
        product2.setSelected(false);
        
        dashboardModel.getProductList().addAll(product1, product2);

        // When
        boolean result = dashboardInteractor.preProductPrintValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void preProductPrintValidation_ShouldReturnFalse_WhenAllProductsAreSelected() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 3, 5);
        
        product1.setSelected(true);
        product2.setSelected(true);
        
        dashboardModel.getProductList().addAll(product1, product2);

        // When
        boolean result = dashboardInteractor.preProductPrintValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void preProductFetchValidation_ShouldReturnTrue_WhenProductListIsEmpty() {
        // Given - empty product list by default

        // When
        boolean result = dashboardInteractor.preProductFetchValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void preProductFetchValidation_ShouldReturnFalse_WhenProductListHasItems() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        dashboardModel.getProductList().add(product1);

        // When
        boolean result = dashboardInteractor.preProductFetchValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void preProductFetchValidation_ShouldReturnFalse_WhenProductListHasMultipleItems() {
        // Given
        ProductEntry product1 = new ProductEntry(1, "image1", "Product1", "Category1", 5, 10);
        ProductEntry product2 = new ProductEntry(2, "image2", "Product2", "Category2", 3, 5);
        
        dashboardModel.getProductList().addAll(product1, product2);

        // When
        boolean result = dashboardInteractor.preProductFetchValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void preUserFetchValidation_ShouldReturnTrue_WhenUserListIsEmpty() {
        // Given - empty user list by default

        // When
        boolean result = dashboardInteractor.preUserFetchValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void preUserFetchValidation_ShouldReturnFalse_WhenUserListHasItems() {
        // Given
        UserEntry user1 = new UserEntry(1, "user1", "admin", "pending");
        dashboardModel.getUserList().add(user1);

        // When
        boolean result = dashboardInteractor.preUserFetchValidation();

        // Then
        assertFalse(result);
    }

    @Test
    void preUserFetchValidation_ShouldReturnFalse_WhenUserListHasMultipleItems() {
        // Given
        UserEntry user1 = new UserEntry(1, "user1", "admin", "pending");
        UserEntry user2 = new UserEntry(2, "user2", "user", "pending");
        
        dashboardModel.getUserList().addAll(user1, user2);

        // When
        boolean result = dashboardInteractor.preUserFetchValidation();

        // Then
        assertFalse(result);
    }
}