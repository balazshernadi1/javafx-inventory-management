package com.balazsh.inventory.features.dashboard.product;

import com.balazsh.inventory.domain.model.ProductEntry;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.Builder;
import javafx.util.Callback;

import java.util.function.Consumer;

/**
 * Product view builder constructing the product management interface.
 * Handles product table display, search functionality, transaction forms,
 * and loading states for inventory operations.
 */
public class ProductViewBuilder implements Builder<Region> {

    private final ProductModel productModel; // Product state and form data
    private final Consumer<Runnable> printDetailsAction; // Print product details callback
    private final Consumer<Runnable> printStockAction; // Print stock report callback
    private final Consumer<Runnable> showSaleForm; // Show sale form callback
    private final Consumer<Runnable> sellProductAction; // Process sale callback
    private final Consumer<Runnable> showBuyForm; // Show purchase form callback
    private final Consumer<Runnable> buyProductAction; // Process purchase callback
    private final Consumer<Runnable> refreshAction; // Refresh data callback

    public ProductViewBuilder(ProductModel productModel,
                              Consumer<Runnable> printDetailsAction,
                              Consumer<Runnable> printStockAction,
                              Consumer<Runnable> showSaleForm,
                              Consumer<Runnable> sellProductAction,
                              Consumer<Runnable> showBuyForm,
                              Consumer<Runnable> buyProductAction,
                              Consumer<Runnable> refreshAction) {
        this.productModel = productModel;
        this.printDetailsAction = printDetailsAction;
        this.printStockAction = printStockAction;
        this.showSaleForm = showSaleForm;
        this.sellProductAction = sellProductAction;
        this.showBuyForm = showBuyForm;
        this.buyProductAction = buyProductAction;
        this.refreshAction = refreshAction;
    }

    /** Builds the complete product management interface with table, forms, and controls */
    @Override
    public Region build() {
        StackPane root = new StackPane();

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        mainContent.disableProperty().bind(productModel.isLoadingProperty());

        TextField searchField = createSearchField();

        TableView<ProductEntry> tableView = createProductTableView(searchField);

        HBox buttonPanel = createButtonPanel();

        mainContent.getChildren().addAll(searchField, tableView, buttonPanel);

        root.getChildren().addAll(mainContent, createLoadingScreen(), createProductBuyForm(), createProductSaleForm());

        return root;
    }

    /** Creates reusable form root container with overlay styling */
    private VBox createFormRoot(){
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        root.setPadding(new Insets(50));
        root.getStylesheets().add("/auth_page.css");
        
        return root;
    }

    /** Creates styled form container with consistent sizing and appearance */
    private VBox createFormContainer() {
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        formContainer.setMaxHeight(500);
        formContainer.setPrefWidth(400);
        formContainer.getStyleClass().add("form-container");
        return formContainer;
    }

    /** Creates product purchase form with quantity input and confirmation buttons */
    private Node createProductBuyForm(){
        VBox formRoot = createFormRoot();
        formRoot.visibleProperty().bind(productModel.showBuyFormProperty());
        formRoot.managedProperty().bind(formRoot.visibleProperty());

        VBox formContainer = createFormContainer();
        
        Label title = new Label("Buy Product (Restock)");
        title.getStyleClass().add("main-header");
        
        VBox fieldsContainer = new VBox(15);
        fieldsContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label quantityLabel = new Label("Quantity:");
        quantityLabel.getStyleClass().add("text-field-label");
        
        TextField quantityField = new TextField();
        quantityField.getStyleClass().add("form-field");
        quantityField.setPromptText("Enter quantity to purchase");
        quantityField.textProperty().bindBidirectional(productModel.buyQuantityProperty());
        quantityField.setMaxWidth(Double.MAX_VALUE);
        
        fieldsContainer.getChildren().addAll(quantityLabel, quantityField);

        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button confirm = new Button("Confirm Purchase");
        confirm.getStyleClass().add("success-button");
        
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("danger-button");
        cancel.setOnAction(e -> {
            productModel.showBuyFormProperty().set(false);
            productModel.buyQuantityProperty().set("");
        });

        // Confirm purchase with loading state and form reset
        confirm.setOnAction(e -> {
            formRoot.setDisable(true);
            productModel.isLoadingProperty().set(true);
            buyProductAction.accept(() -> {
                formRoot.setDisable(false);
                productModel.showBuyFormProperty().set(false);
                productModel.isLoadingProperty().set(false);
            });
        });
        
        buttonContainer.getChildren().addAll(confirm, cancel);

        formContainer.getChildren().addAll(title, fieldsContainer, buttonContainer);
        formRoot.getChildren().add(formContainer);
        
        return formRoot;
    }

    /** Creates product sale form with price and quantity inputs */
    private Node createProductSaleForm(){
        VBox formRoot = createFormRoot();
        formRoot.visibleProperty().bind(productModel.showSaleFormProperty());
        formRoot.managedProperty().bind(formRoot.visibleProperty());

        VBox formContainer = createFormContainer();

        Label title = new Label("Sell Product");
        title.getStyleClass().add("main-header");

        VBox fieldsContainer = new VBox(15);
        fieldsContainer.setAlignment(Pos.CENTER_LEFT);

        Label priceLabel = new Label("Price (pennies):");
        priceLabel.getStyleClass().add("text-field-label");
        
        TextField priceField = new TextField();
        priceField.getStyleClass().add("form-field");
        priceField.setPromptText("Enter product price");
        priceField.textProperty().bindBidirectional(productModel.priceProperty());
        priceField.setMaxWidth(Double.MAX_VALUE);

        Label quantityLabel = new Label("Quantity:");
        quantityLabel.getStyleClass().add("text-field-label");
        
        ComboBox<Integer> quantityField = new ComboBox<>();
        quantityField.getStyleClass().add("form-combo");
        quantityField.setPromptText("Select quantity");
        quantityField.itemsProperty().set(productModel.getQuantities());
        quantityField.setMaxWidth(Double.MAX_VALUE);
        productModel.quantityProperty().bind(quantityField.valueProperty());

        fieldsContainer.getChildren().addAll(
            priceLabel, priceField,
            quantityLabel, quantityField
        );

        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button confirm = new Button("Confirm Sale");
        confirm.getStyleClass().add("form-button");
        
        Button cancel = new Button("Cancel");

        cancel.getStyleClass().add("secondary-button");
        cancel.setOnAction(e -> {
            productModel.showSaleFormProperty().set(false);
            productModel.priceProperty().set("");
        });

        // Confirm sale with loading state and form reset
        confirm.setOnAction(e -> {
            formRoot.setDisable(true);
            productModel.isLoadingProperty().set(true);
            sellProductAction.accept(() -> {
                formRoot.setDisable(false);
                productModel.showSaleFormProperty().set(false);
                productModel.isLoadingProperty().set(false);
            });
        });
        
        buttonContainer.getChildren().addAll(confirm, cancel);

        formContainer.getChildren().addAll(title, fieldsContainer, buttonContainer);
        formRoot.getChildren().add(formContainer);
        
        return formRoot;
    }

    /** Creates loading overlay with progress indicator and status messages */
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
                Bindings.selectString(productModel.resultObjectPropertyProperty(), "message"));

        ProgressBar progressIndicator = new ProgressBar();
        progressIndicator.progressProperty().bind(productModel.progressProperty());
        progressIndicator.setStyle("-fx-progress-color: green;");

        loadingScreenContent.getChildren().addAll(loadingLabel, loadingStatus, progressIndicator);

        loadingScreenRoot.setCenter(loadingScreenContent);

        loadingScreenRoot.visibleProperty().bind(productModel.isLoadingProperty());

        return loadingScreenRoot;
    }

    /** Creates search field for filtering products by name, category, or ID */
    private TextField createSearchField() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search products by name, category, or ID...");
        searchField.getStyleClass().add("form-field");
        searchField.setPrefWidth(400);

        return searchField;
    }

    /** Creates product table with search filtering, selection, and image display */
    private TableView<ProductEntry> createProductTableView(TextField searchField) {
        TableView<ProductEntry> tableView = new TableView<>();
        tableView.getStyleClass().add("modern-table");

        FilteredList<ProductEntry> filteredData = new FilteredList<>(productModel.getProductEntries(), p -> true);

        // Real-time search filtering by name, category, or ID
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(product -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (product.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (product.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return String.valueOf(product.getId()).contains(lowerCaseFilter);
            });
        });

        tableView.setItems(filteredData);

        // Prevent extra column
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Selection column with checkboxes
        TableColumn<ProductEntry, Boolean> selectionColumn = new TableColumn<>("Select");
        selectionColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectionColumn.setCellFactory(createCheckBoxCellFactory());
        selectionColumn.setPrefWidth(80);
        selectionColumn.setResizable(false);

        // Image column
        TableColumn<ProductEntry, String> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        imageColumn.setCellFactory(createImageCellFactory());
        imageColumn.setPrefWidth(80);
        imageColumn.setResizable(false);

        // ID column
        TableColumn<ProductEntry, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(60);

        // Name column
        TableColumn<ProductEntry, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        // Category column
        TableColumn<ProductEntry, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setPrefWidth(150);

        // In Stock column
        TableColumn<ProductEntry, Integer> inStockColumn = new TableColumn<>("In Stock");
        inStockColumn.setCellValueFactory(new PropertyValueFactory<>("inStock"));
        inStockColumn.setPrefWidth(100);

        // Available for Purchase column
        TableColumn<ProductEntry, Integer> availableColumn = new TableColumn<>("On Sale");
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfProductsAvailableForPurchase"));
        availableColumn.setPrefWidth(100);

        tableView.getColumns().addAll(selectionColumn, imageColumn, idColumn, nameColumn,
                                     categoryColumn, inStockColumn, availableColumn);

        return tableView;
    }

    /** Creates checkbox cell factory for product selection with proper binding */
    private Callback<TableColumn<ProductEntry, Boolean>, TableCell<ProductEntry, Boolean>> createCheckBoxCellFactory() {
        return column -> new TableCell<ProductEntry, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            private ProductEntry currentEntry = null;

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                // Unbind from previous entry
                if (currentEntry != null) {
                    checkBox.selectedProperty().unbindBidirectional(currentEntry.selectedProperty());
                    currentEntry = null;
                }

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    checkBox.setSelected(false);
                } else {
                    currentEntry = getTableRow().getItem();
                    checkBox.selectedProperty().bindBidirectional(currentEntry.selectedProperty());
                    setGraphic(checkBox);
                }
            }
        };
    }

    /** Creates image cell factory for product thumbnail display with error handling */
    private Callback<TableColumn<ProductEntry, String>, TableCell<ProductEntry, String>> createImageCellFactory() {
        return column -> new TableCell<ProductEntry, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.getStyleClass().add("product-image");
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image image = new Image(imagePath);
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        };
    }

    /** Creates button panel with product operations and form disabled during modal display */
    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(12);
        buttonPanel.getStyleClass().add("button-panel");
        buttonPanel.disableProperty().bind(Bindings.or(productModel.showBuyFormProperty(), productModel.showSaleFormProperty()));

        Button selectAllButton = new Button("Select All");
        selectAllButton.getStyleClass().add("secondary-button");
        selectAllButton.setOnAction(e -> selectAllItems(true));

        Button deselectAllButton = new Button("Deselect All");
        deselectAllButton.getStyleClass().add("secondary-button");
        deselectAllButton.setOnAction(e -> selectAllItems(false));

        Button printDetailsButton = new Button("Print Details");
        printDetailsButton.getStyleClass().add("action-button");

        Button printStockButton = new Button("Print Stock");
        printStockButton.getStyleClass().add("action-button");

        Button sellProductButton = new Button("Sell Product");
        sellProductButton.getStyleClass().add("success-button");

        Button buyProductButton = new Button("Buy Product");
        buyProductButton.getStyleClass().add("action-button");

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("action-button");

        buttonPanel.getChildren().addAll(
            selectAllButton, deselectAllButton, printDetailsButton,
            printStockButton, sellProductButton, buyProductButton, refreshButton
        );

        // Button actions with loading states
        printDetailsButton.setOnAction(e -> {
            productModel.isLoadingProperty().set(true);
            printDetailsAction.accept(()->{
                productModel.isLoadingProperty().set(false);
            });
        });

        printStockButton.setOnAction(e -> {
            productModel.isLoadingProperty().set(true);
            printStockAction.accept(()->{
                productModel.isLoadingProperty().set(false);
            });
        });

        sellProductButton.setOnAction(e -> {
            showSaleForm.accept(()->{});
        });

        buyProductButton.setOnAction(e -> {
            showBuyForm.accept(()->{});
        });

        refreshButton.setOnAction(e -> {
            productModel.isLoadingProperty().set(true);
            refreshAction.accept(()->{
                productModel.isLoadingProperty().set(false);
            });
        });

        return buttonPanel;
    }
    
    /** Utility method to select or deselect all products in the table */
    private void selectAllItems(boolean selected) {
        for (ProductEntry entry : productModel.getProductEntries()) {
            entry.selectedProperty().set(selected);
        }
    }
}
