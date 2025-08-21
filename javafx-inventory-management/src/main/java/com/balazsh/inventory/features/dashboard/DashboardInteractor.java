package com.balazsh.inventory.features.dashboard;

import com.balazsh.inventory.dao.ProductDAOImpl;
import com.balazsh.inventory.dao.UserDaoImpl;
import com.balazsh.inventory.domain.model.*;
import com.balazsh.inventory.domain.service.ProductService;
import com.balazsh.inventory.domain.service.UserService;
import com.balazsh.inventory.util.exceptions.ProductProcessingException;
import com.balazsh.inventory.util.exceptions.UserException;
import com.balazsh.inventory.util.enums.OPERATION;
import com.balazsh.inventory.util.enums.RESOURCE;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Dashboard interactor handling business logic for inventory management operations.
 * Coordinates product transactions, user management, data loading, and permission validation.
 * Integrates with product and user services for database operations and provides
 * sample data generation for testing purposes.
 */
public class DashboardInteractor {

    private final DashboardModel dashboardModel; // Shared dashboard state
    private final Random random = new Random(); // Sample data generation
    private final ProductService productService; // Product business operations
    private final UserService userService; // User management operations

    public DashboardInteractor(DashboardModel dashboardModel) {
        this.dashboardModel = dashboardModel;
        productService = new ProductService(new ProductDAOImpl(), new UserDaoImpl());
        userService = new UserService(new UserDaoImpl());
    }

    /** Initializes dashboard with sample product and user data for testing */
    public void createDashboardData(){
        dashboardModel.getProductList().addAll(generateSampleProducts());
        dashboardModel.getUserList().addAll(generateSampleUsers());
    }

    /** Processes product sale transaction with error handling and result feedback */
    public void sellProducts(){
        try{
            ProductSale sale = dashboardModel.getProductSaleObjectProperty();
            productService.sellProduct(sale.productId(), dashboardModel.getActiveUserDetailsObjectProperty().username(), sale.quantity(), sale.price());
            setResult("success", "Product sale completed");
        }catch (ProductProcessingException e){
            setResult("failed", e.getMessage());
        }catch (Exception e){
            setResult("failed", "Something went wrong");
        }
    }

    /** Processes product purchase transaction with error handling and result feedback */
    public void buyProducts(){
        try{
            ProductPurchase purchase = dashboardModel.getProductPurchaseObjectProperty();
            productService.buyProduct(purchase.productId(), dashboardModel.getActiveUserDetailsObjectProperty().username(), purchase.quantity());
            setResult("success", "Product purchase completed successfully");
        }catch (ProductProcessingException e){
            setResult("failed", e.getMessage());
        }catch (Exception e){
            setResult("failed", "Something went wrong");
        }
    }

    /** Prints stock details for selected products to file with error handling */
    public void printStockDetails(){
        try{
            List<Integer> productIds = dashboardModel.getProductList()
                    .stream()
                    .filter(ProductEntry::isSelected)
                    .map(ProductEntry::getId).toList();
            productService.printProductStockDetailsToFile(productIds);
            setResult("success", "Product stock details printed successfully");
        }catch (ProductProcessingException e){
            setResult("failure", e.getMessage());
        }catch (Exception e){
            setResult("failure", "Something went wrong");
        }
    }

    /** Prints general product details for selected products to file with error handling */
    public void printDetails(){
        try{
            List<Integer> productIds = dashboardModel.getProductList()
                    .stream()
                    .filter(ProductEntry::isSelected)
                    .map(ProductEntry::getId).toList();

            productService.printProductDetailsToFile(productIds);
            setResult("success", "Product details printed successfully");
        }catch (ProductProcessingException e){
            setResult("failed", e.getMessage());
        }catch (Exception e){
            setResult("failed", "Something went wrong");
        }
    }

    /** Fetches fresh product data from service and updates UI on JavaFX thread */
    public void fetchProductEntries(){
        List<ProductEntry> productEntries = productService.fetchProducts();
        Platform.runLater(() -> {
            if(!dashboardModel.getProductList().isEmpty()){
                dashboardModel.getProductList().clear();
            }
            dashboardModel.getProductList().addAll(productEntries);
        });
    }

    /** Fetches pending user data from service and updates UI on JavaFX thread */
    public void fetchUserEntries(){
        List<UserEntry> userEntries = userService.getAllPendingUsers();
        Platform.runLater(() -> {
            if(!dashboardModel.getUserList().isEmpty()){
                dashboardModel.getUserList().clear();
            }
            dashboardModel.getUserList().addAll(userEntries);
        });
    }

    /** Loads product entries with error handling and user feedback */
    public void loadProductEntries(){
        try{
            fetchProductEntries();
            setResult("success", "Product entries loaded successfully");
        }catch (ProductProcessingException e){
            setResult("failed", e.getMessage());
        }catch (Exception e){
            setResult("failed", "Something went wrong");
        }
    }

    /** Updates dashboard result property on JavaFX thread for UI feedback */
    private void setResult(String status, String message){
        Platform.runLater(() -> {
            dashboardModel.resultObjectPropertyProperty().set(new Result(status, message));
        });
    }

    /** Validates if products are available and selected for printing operations */
    public boolean preProductPrintValidation(){
        return dashboardModel.getProductList().isEmpty() || dashboardModel.getProductList().stream().noneMatch(ProductEntry::isSelected);
    }

    /** Validates if product list is empty before fetch operations */
    public boolean preProductFetchValidation(){
        return dashboardModel.getProductList().isEmpty();
    }

    /** Validates if user list is empty before fetch operations */
    public boolean preUserFetchValidation(){
        return dashboardModel.getUserList().isEmpty();
    }

    /** Loads user entries with error handling and user feedback */
    public void loadUserEntries(){
        try{
            fetchUserEntries();
            setResult("success", "User entries loaded successfully");
        }catch (UserException e){
            setResult("failed", e.getMessage());
        }catch (Exception e){
            setResult("failed", "Something went wrong");
        }
    }

    /** Generates sample user data with realistic usernames, roles, and status distribution */
    private List<UserEntry> generateSampleUsers() {
        List<UserEntry> users = new ArrayList<>();

        String[] usernames = {
                "john.doe", "jane.smith", "mike.johnson", "sarah.williams", "david.brown",
                "emily.davis", "chris.miller", "lisa.wilson", "tom.moore", "anna.taylor",
                "james.anderson", "maria.thomas", "robert.jackson", "michelle.white",
                "kevin.harris", "laura.martin", "daniel.thompson", "jessica.garcia",
                "mark.martinez", "amanda.robinson", "paul.clark", "stephanie.rodriguez",
                "anthony.lewis", "nicole.lee", "steven.walker"
        };

        String[] roles = {
                "Administrator", "Manager", "Employee", "Supervisor", "Analyst",
                "Coordinator", "Assistant", "Specialist", "Director", "Operator"
        };

        String[] statuses = {
                "Active", "Pending", "Suspended", "Inactive", "Pending Approval"
        };

        for (int i = 0; i < 20; i++) {
            UserEntry user = new UserEntry();
            user.setId(i + 1);
            user.setUsername(usernames[i % usernames.length]);
            user.setRoleSelected(roles[random.nextInt(roles.length)]);

            // Weight status distribution - more active users
            String status;
            int statusRoll = random.nextInt(100);
            if (statusRoll < 70) {
                status = "Active";
            } else if (statusRoll < 85) {
                status = "Pending Approval";
            } else if (statusRoll < 95) {
                status = "Pending";
            } else if (statusRoll < 98) {
                status = "Inactive";
            } else {
                status = "Suspended";
            }

            user.setAccountStatus(status);
            user.setUserSelected(false);

            users.add(user);
        }

        return users;
    }

    /** Generates sample product data with realistic names, categories, and stock levels */
    private List<ProductEntry> generateSampleProducts() {
        List<ProductEntry> products = new ArrayList<>();

        String[] productNames = {
                "Apple MacBook Pro", "Dell XPS 13", "HP Spectre x360", "Lenovo ThinkPad T14",
                "Samsung Galaxy Book", "ASUS ZenBook", "Microsoft Surface Laptop", "Acer Swift 3",
                "Gaming Mouse", "Mechanical Keyboard", "4K Monitor", "Wireless Headphones",
                "USB-C Hub", "External SSD", "Webcam HD", "Bluetooth Speaker",
                "Tablet Stand", "Phone Charger", "HDMI Cable", "Power Bank",
                "Smart Watch", "Fitness Tracker", "VR Headset", "Drone Camera",
                "Action Camera", "Ring Light", "Microphone", "Graphics Tablet"
        };

        String[] categories = {
                "Laptops", "Accessories", "Audio", "Storage", "Monitors",
                "Input Devices", "Cables", "Mobile", "Wearables", "Camera Equipment"
        };

        String[] imageUrls = {
                "/images/laptop1.jpg", "/images/laptop2.jpg", "/images/mouse.jpg",
                "/images/keyboard.jpg", "/images/monitor.jpg", "/images/headphones.jpg",
                "/images/hub.jpg", "/images/ssd.jpg", "/images/webcam.jpg",
                "/images/speaker.jpg", "/images/stand.jpg", "/images/charger.jpg",
                "/images/cable.jpg", "/images/powerbank.jpg", "/images/watch.jpg",
                "/images/tracker.jpg", "/images/vr.jpg", "/images/drone.jpg"
        };

        for (int i = 0; i < 25; i++) {
            ProductEntry product = new ProductEntry();
            product.setId(i + 1);
            product.setName(productNames[i % productNames.length]);
            product.setCategory(categories[random.nextInt(categories.length)]);
            product.setImage(imageUrls[random.nextInt(imageUrls.length)]);
            product.setInStock(random.nextInt(100) + 10); // 10-109 items in stock
            product.setNumberOfProductsAvailableForPurchase(
                    Math.min(product.getInStock(), random.nextInt(product.getInStock()) + 1)
            );
            product.setSelected(false);

            products.add(product);
        }

        return products;
    }

    /** Deletes selected user with error handling and result feedback */
    public void deleteUser(){
        try {
            userService.deleteUser(dashboardModel.getSelectedUser().getId());
            setResult("success", "User Approved");
        }catch (UserException e) {
            setResult("failed", e.getMessage());
        }catch (Exception e) {
            setResult("failed", "Something went wrong");
        }
    }

    /** Checks if current user has permission to view user management features */
    public boolean hasViewUserPermission(){
        return Optional.ofNullable(dashboardModel.activeUserDetailsObjectPropertyProperty().get())
                .map(ActiveUserDetails::permissions)
                .map(permissions -> permissions.get(RESOURCE.USER))
                .map(operations -> operations.contains(OPERATION.VIEW))
                .orElse(false);
    }

    /** Approves selected user's role with error handling and result feedback */
    public void approveUser(){
        try {
            userService.approveUserRole(dashboardModel.getSelectedUser().getId());
            setResult("success", "User Approved");
        }catch (UserException e) {
            setResult("failed", e.getMessage());
        }catch (Exception e) {
            setResult("failed", "Something went wrong");
        }
    }
}
