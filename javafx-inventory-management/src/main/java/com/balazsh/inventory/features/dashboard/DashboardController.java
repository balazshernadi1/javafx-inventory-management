package com.balazsh.inventory.features.dashboard;

import com.balazsh.inventory.domain.model.ActiveUserDetails;
import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.features.dashboard.product.ProductController;
import com.balazsh.inventory.features.dashboard.user.UserController;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

/**
 * Dashboard controller orchestrating inventory management operations.
 * Coordinates between product and user management, handles async operations,
 * and manages permissions and data loading.
 */
public class DashboardController {

    private final DashboardInteractor dashboardInteractor; // Business logic for dashboard operations
    private final DashboardModel dashboardModel; // Shared state and data collections
    private final DashboardViewBuilder dashboardViewBuilder; // UI coordination

    private final UserController userController; // User management child controller
    private final ProductController productController; // Product management child controller

    /**
     * Creates dashboard controller with child controllers and shared state binding.
     * Sets up coordination between product and user management features.
     */
    public DashboardController(Runnable switchToAuthentication, ObjectProperty<ActiveUserDetails> activeUserDetailsObjectProperty) {
        this.dashboardModel = new DashboardModel();
        this.dashboardInteractor = new DashboardInteractor(dashboardModel);

        // Initialize user management controller with shared data and operation callbacks
        this.userController = new UserController(
                dashboardModel.getUserList(),
                this::deleteUser,
                this::approveUser,
                dashboardModel.selectedUserProperty(),
                dashboardModel.resultObjectPropertyProperty(),
                dashboardModel.progressProperty()
        );

        // Initialize product management controller with shared data and operation callbacks
        this.productController = new ProductController(
                dashboardModel.getProductList(),
                dashboardModel.resultObjectPropertyProperty(),
                dashboardModel.progressProperty(),
                dashboardModel.productPurchaseObjectPropertyProperty(),
                dashboardModel.productSaleObjectPropertyProperty(),
                this::printProductDetails,
                this::printStockDetails,
                this::sellProduct,
                this::buyProduct,
                this::refreshProducts);

        // Initialize view builder with child views and navigation callbacks
        this.dashboardViewBuilder =
                new DashboardViewBuilder(
                        dashboardModel,
                        userController.getView(),
                        productController.getView(),
                        this::fetchProducts,
                        this::fetchUsers,
                        switchToAuthentication
                );
        
        // Bind active user details for permission checks and user context
        dashboardModel.activeUserDetailsObjectPropertyProperty().bind(activeUserDetailsObjectProperty);
    }

    /** Refreshes product data asynchronously*/
    private void refreshProducts(Runnable postAsync) {
        Task<Void> refreshTask = new Task() {
            @Override
            protected Void call() throws Exception {
                dashboardInteractor.fetchProductEntries();
                return null;
            }
        };

        dashboardModel.progressProperty().bind(refreshTask.progressProperty());

        refreshTask.setOnSucceeded(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        refreshTask.setOnFailed(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        Thread thread = new Thread(refreshTask);
        thread.setName("Refresh");
        thread.start();
    }

    /** Prints selected product details asynchronously*/
    private void printProductDetails(Runnable postAsync){
        if (dashboardInteractor.preProductPrintValidation()){
            postAsync.run();
            return;
        }

        Task<Void> printTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                dashboardInteractor.printDetails();
                return null;
            }
        };

        dashboardModel.progressProperty().bind(printTask.progressProperty());

        printTask.setOnSucceeded(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        printTask.setOnFailed(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        Thread thread = new Thread(printTask);
        thread.setName("Print Product Details");
        thread.start();
    }

    /** Fetches product data asynchronously */
    private void fetchProducts(Runnable postAsync){
        if (!dashboardInteractor.preProductFetchValidation()){
            postAsync.run();
            return;
        }

        dashboardModel.isLoadingProperty().set(true);

        Task<Void> fetchProductsTask = new Task<>() {
            @Override
            protected Void call() {
                dashboardInteractor.loadProductEntries();
                return null;
            }
        };

        dashboardModel.progressProperty().bind(fetchProductsTask.progressProperty());

        fetchProductsTask.setOnSucceeded(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        fetchProductsTask.setOnFailed(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });

        Thread thread = new Thread(fetchProductsTask);
        thread.setName("Fetch Products");
        thread.start();
    }

    /** Fetches user data asynchronously with permission checks and validation */
    private void fetchUsers(Runnable postAsync) {
        // Check user permissions before allowing access to user management
        if (!dashboardInteractor.hasViewUserPermission()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Permission error");
            alert.setContentText("Permission denied");
            alert.show();
            dashboardModel.productPageSelectedProperty().setValue(false);
            dashboardModel.userPageSelectedProperty().setValue(false);
            postAsync.run();
            return;
        }

        if (!dashboardInteractor.preUserFetchValidation()){
            postAsync.run();
            return;
        }

        dashboardModel.isLoadingProperty().set(true);

        Task<Void> fetchUserTask = new Task<>() {
            @Override
            protected Void call() {
                dashboardInteractor.loadUserEntries();
                return null;
            }
        };

        dashboardModel.progressProperty().bind(fetchUserTask.progressProperty());

        fetchUserTask.setOnSucceeded(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        fetchUserTask.setOnFailed(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });

        Thread thread = new Thread(fetchUserTask);
        thread.setName("Fetch Users");
        thread.start();
    }

    /** Prints stock details asynchronously with validation */
    private void printStockDetails(Runnable postAsync){
        if (dashboardInteractor.preProductPrintValidation()){
            postAsync.run();
            return;
        }
        Task<Void> printStockTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                dashboardInteractor.printStockDetails();
                return null;
            }
        };

        dashboardModel.progressProperty().bind(printStockTask.progressProperty());

        printStockTask.setOnSucceeded(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        printStockTask.setOnFailed(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        Thread thread = new Thread(printStockTask);
        thread.setName("Print Stock Details");
        thread.start();
    }

    /** Processes product sale and refreshes inventory data */
    private void sellProduct(Runnable postAsync){
        Task<Void> sellProductTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                dashboardInteractor.sellProducts();
                dashboardInteractor.fetchProductEntries();  // Refresh after sale
                return null;
            }
        };

        dashboardModel.progressProperty().bind(sellProductTask.progressProperty());
        sellProductTask.setOnSucceeded(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });

        sellProductTask.setOnFailed(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        Thread thread = new Thread(sellProductTask);
        thread.setName("Sell Product");
        thread.start();
    }

    /** Processes product purchase and refreshes inventory data */
    private void buyProduct(Runnable postAsync){
        Task<Void> buyProductTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                dashboardInteractor.buyProducts();
                dashboardInteractor.fetchProductEntries();  // Refresh after purchase
                return null;
            }
        };

        dashboardModel.progressProperty().bind(buyProductTask.progressProperty());
        buyProductTask.setOnSucceeded(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });

        buyProductTask.setOnFailed(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        Thread thread = new Thread(buyProductTask);
        thread.setName("Buy Product");
        thread.start();
    }

    /** Generates sample data for testing and development */
    public void generateData(){
        dashboardInteractor.createDashboardData();
    }

    /** Deletes selected user and refreshes user list */
    private void deleteUser(Runnable postAsync){
        Task<Void> deleteUserTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                dashboardInteractor.deleteUser();
                dashboardInteractor.fetchUserEntries();  // Refresh after deletion
                return null;
            }
        };

        dashboardModel.progressProperty().bind(deleteUserTask.progressProperty());
        deleteUserTask.setOnSucceeded(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
            dashboardModel.resultObjectPropertyProperty().set(new Result("", ""));  // Clear result
        });
        deleteUserTask.setOnFailed(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
            dashboardModel.resultObjectPropertyProperty().set(new Result("", ""));  // Clear result
        });
        Thread thread = new Thread(deleteUserTask);
        thread.setName("Delete User");
        thread.start();
    }

    /** Approves selected user and refreshes user list */
    private void approveUser(Runnable postAsync){
        Task<Void> approveUserTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                dashboardInteractor.approveUser();
                dashboardInteractor.fetchUserEntries();  // Refresh after approval
                return null;
            }
        };

        dashboardModel.progressProperty().bind(approveUserTask.progressProperty());
        approveUserTask.setOnSucceeded(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        approveUserTask.setOnFailed(event -> {
            dashboardModel.progressProperty().unbind();
            postAsync.run();
        });
        Thread thread = new Thread(approveUserTask);
        thread.setName("Approve User");
        thread.start();
    }

    public Region getView(){
        return dashboardViewBuilder.build();
    }
}
