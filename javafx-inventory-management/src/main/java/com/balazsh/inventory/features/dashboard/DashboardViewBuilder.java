package com.balazsh.inventory.features.dashboard;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * Dashboard view builder constructing the main inventory management interface.
 * Coordinates navigation between product and user management views with header,
 * navigation panel, loading states, and logout functionality.
 */
public class DashboardViewBuilder implements Builder<Region> {

    private final DashboardModel dashboardModel; // Dashboard state and data
    private final Region productView; // Product management UI component
    private final Region userView; // User management UI component
    private final Consumer<Runnable> fetchProducts; // Async product data loading
    private final Consumer<Runnable> fetchUsers; // Async user data loading
    private final Runnable switchToAuthentication; // Logout navigation callback

    public DashboardViewBuilder(DashboardModel dashboardModel,
                                Region userView,
                                Region productView,
                                Consumer<Runnable> fetchProducts,
                                Consumer<Runnable> fetchUsers,
                                Runnable switchToAuthentication) {
        this.dashboardModel = dashboardModel;
        this.userView = userView;
        this.productView = productView;
        this.fetchProducts = fetchProducts;
        this.fetchUsers = fetchUsers;
        this.switchToAuthentication = switchToAuthentication;
    }

    /** Builds the complete dashboard layout with header, navigation, and content areas */
    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        String cssFile = getClass().getResource("/dashboard.css").toExternalForm();
        root.getStylesheets().add(cssFile);

        VBox headerPanel = createHeaderPanel();

        VBox navigationPanel = createNavigationPanel();

        StackPane centerContent = createCenterContent();

        root.setTop(headerPanel);
        root.setLeft(navigationPanel);
        root.setCenter(centerContent);
        
        return root;
    }
    
    /** Creates header panel with app title and personalized welcome message */
    private VBox createHeaderPanel() {
        VBox headerPanel = new VBox();
        headerPanel.getStyleClass().add("dashboard-header");
        headerPanel.setAlignment(Pos.CENTER_LEFT);
        
        Label mainHeader = new Label("Inventory Management");
        mainHeader.getStyleClass().add("dashboard-main-title");
        
        Label subHeader = new Label();
        subHeader.getStyleClass().add("dashboard-subtitle");
        
        subHeader.textProperty().bind(
            dashboardModel.activeUserDetailsObjectPropertyProperty()
                .map(activeUser -> activeUser != null ? "Welcome, " + activeUser.username() : "Welcome, Guest")
        );
        
        headerPanel.getChildren().addAll(mainHeader, subHeader);
        
        return headerPanel;
    }
    
    /** Creates navigation panel with toggle buttons for page switching and logout */
    private VBox createNavigationPanel() {
        VBox navigationPanel = new VBox(15);
        navigationPanel.getStyleClass().add("navigation-panel");
        navigationPanel.setAlignment(Pos.TOP_CENTER);
        
        ToggleGroup navigationGroup = new ToggleGroup();
        
        ToggleButton productButton = new ToggleButton("Product");
        productButton.getStyleClass().add("nav-toggle-button");
        productButton.setToggleGroup(navigationGroup);
        productButton.selectedProperty().bindBidirectional(dashboardModel.productPageSelectedProperty());
        
        ToggleButton userButton = new ToggleButton("User");
        userButton.getStyleClass().add("nav-toggle-button");
        userButton.setToggleGroup(navigationGroup);
        userButton.selectedProperty().bindBidirectional(dashboardModel.userPageSelectedProperty());
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("danger-button");
        logoutButton.setPrefWidth(180);
        logoutButton.setOnAction(e -> {
            switchToAuthentication.run();
        });
        
        navigationPanel.getChildren().addAll(productButton, userButton, spacer, logoutButton);

        // Product button action with loading state and button disabling
        productButton.setOnAction(e -> {
            productButton.setDisable(true);
            userButton.setDisable(true);
            logoutButton.setDisable(true);
            fetchProducts.accept(() -> {
                productButton.setDisable(false);
                userButton.setDisable(false);
                logoutButton.setDisable(false);

                PauseTransition hideLoading = new PauseTransition(Duration.seconds(3));
                hideLoading.playFromStart();

                hideLoading.setOnFinished(event1 ->{
                    dashboardModel.isLoadingProperty().set(false);
                    dashboardModel.progressProperty().unbind();
                });
            });
        });

        // User button action with loading state and button disabling
        userButton.setOnAction(e -> {
            productButton.setDisable(true);
            userButton.setDisable(true);
            logoutButton.setDisable(true);
            fetchUsers.accept(() -> {
                productButton.setDisable(false);
                userButton.setDisable(false);
                logoutButton.setDisable(false);
                PauseTransition hideLoading = new PauseTransition(Duration.seconds(3));
                hideLoading.playFromStart();

                hideLoading.setOnFinished(event1 ->{
                    dashboardModel.isLoadingProperty().set(false);
                    dashboardModel.progressProperty().unbind();
                });
            });
        });

        return navigationPanel;
    }

    /** Creates loading overlay with progress bar and status messages */
    private Node createLoadingScreen(){
        BorderPane loadingScreenRoot = new BorderPane();
        loadingScreenRoot.setPadding(new Insets(20));

        VBox loadingScreenContent = new VBox(20);
        loadingScreenContent.setPadding(new Insets(20));
        loadingScreenContent.setAlignment(Pos.CENTER);

        Label loadingLabel = new Label("Loading...");
        loadingLabel.getStyleClass().add("loading-message");

        Label loadingStatus = new Label();
        loadingStatus.textProperty().bind(
                Bindings.selectString(dashboardModel.resultObjectPropertyProperty(), "message"));

        ProgressBar progressIndicator = new ProgressBar();
        progressIndicator.progressProperty().bind(dashboardModel.progressProperty());
        progressIndicator.setStyle("-fx-progress-color: green;");

        loadingScreenContent.getChildren().addAll(loadingLabel, loadingStatus, progressIndicator);

        loadingScreenRoot.setCenter(loadingScreenContent);

        loadingScreenRoot.visibleProperty().bind(dashboardModel.isLoadingProperty());

        return loadingScreenRoot;
    }
    
    /** Creates center content area with view switching and loading state management */
    private StackPane createCenterContent() {
        StackPane centerContent = new StackPane();
        centerContent.getStyleClass().add("content-area");

        // Bind visibility properties for view switching
        productView.visibleProperty().bind(dashboardModel.productPageSelectedProperty());
        userView.visibleProperty().bind(dashboardModel.userPageSelectedProperty());
        
        // Ensure only one view is managed at a time for proper layout
        productView.managedProperty().bind(productView.visibleProperty());
        userView.managedProperty().bind(userView.visibleProperty());

        // Disable views during loading operations
        productView.disableProperty().bind(dashboardModel.isLoadingProperty());
        userView.disableProperty().bind(dashboardModel.isLoadingProperty());
        
        centerContent.getChildren().addAll(userView, productView, createLoadingScreen());
        
        return centerContent;
    }
}
