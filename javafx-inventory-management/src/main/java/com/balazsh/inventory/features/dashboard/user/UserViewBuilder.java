package com.balazsh.inventory.features.dashboard.user;

import com.balazsh.inventory.domain.model.UserEntry;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * User view builder constructing the user management interface.
 * Handles user table display, selection functionality, and administrative
 * actions like user approval and deletion with loading states.
 */
public class UserViewBuilder implements Builder<Region> {

    private final UserModel userModel; // User state and selection data
    private final Consumer<Runnable> deleteAction; // Delete user callback
    private final Consumer<Runnable> approveAction; // Approve user callback

    public UserViewBuilder(UserModel userModel, Consumer<Runnable> deleteAction, Consumer<Runnable> approveAction) {
        this.userModel = userModel;
        this.deleteAction = deleteAction;
        this.approveAction = approveAction;

    }

    /** Builds the complete user management interface with table and action buttons */
    @Override
    public Region build() {
        StackPane root = new StackPane();

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.disableProperty().bind(userModel.isLoadingProperty());
        
        TableView<UserEntry> tableView = createUserTableView();
        
        HBox buttonPanel = createButtonPanel(tableView);
        
        mainContent.getChildren().addAll(tableView, buttonPanel);

        root.getChildren().addAll(mainContent,createLoadingScreen());
        
        return root;
    }

    /** Creates loading overlay with progress indicator and status messages */
    private VBox createLoadingScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.visibleProperty().bind(userModel.isLoadingProperty());

        Label loadingLabel = new Label("Loading...");
        loadingLabel.getStyleClass().add("loading-message");

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(userModel.getResultProperty().messageProperty());

        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(userModel.progressPropertyProperty());

        root.getChildren().addAll(loadingLabel, statusLabel, progressBar);
        return root;
    }
    
    /** Creates user table with selection checkboxes and user information columns */
    private TableView<UserEntry> createUserTableView() {
        TableView<UserEntry> tableView = new TableView<>();
        tableView.getStyleClass().add("modern-table");
        tableView.setItems(userModel.getUserEntries());

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Selection column with checkboxes for user selection
        TableColumn<UserEntry, Boolean> selectionColumn = new TableColumn<>("Select");
        selectionColumn.setCellValueFactory(new PropertyValueFactory<>("userSelected"));
        selectionColumn.setCellFactory(createCheckBoxCellFactory());
        selectionColumn.setPrefWidth(80);
        selectionColumn.setResizable(false);
        
        // User ID column
        TableColumn<UserEntry, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(60);
        
        // Username column
        TableColumn<UserEntry, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setPrefWidth(200);
        
        // Role column
        TableColumn<UserEntry, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleSelected"));
        roleColumn.setPrefWidth(150);
        
        // Account status column
        TableColumn<UserEntry, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("accountStatus"));
        statusColumn.setPrefWidth(150);
        
        tableView.getColumns().addAll(selectionColumn, idColumn, usernameColumn, roleColumn, statusColumn);
        
        return tableView;
    }
    
    /** Creates checkbox cell factory for user selection with proper binding */
    private Callback<TableColumn<UserEntry, Boolean>, TableCell<UserEntry, Boolean>> createCheckBoxCellFactory() {
        return column -> new TableCell<UserEntry, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            private UserEntry currentEntry = null;
            
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                
                // Unbind from previous entry
                if (currentEntry != null) {
                    checkBox.selectedProperty().unbindBidirectional(currentEntry.userSelectedProperty());
                    currentEntry = null;
                }
                
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    checkBox.setSelected(false);
                } else {
                    currentEntry = getTableRow().getItem();
                    checkBox.selectedProperty().bindBidirectional(currentEntry.userSelectedProperty());
                    setGraphic(checkBox);
                }
            }
        };
    }
    
    /** Creates administrative action buttons with loading state management */
    private HBox createButtonPanel(TableView<UserEntry> tableView) {
        HBox buttonPanel = new HBox(12);
        buttonPanel.getStyleClass().add("button-panel");
        
        Button approveUserButton = new Button("Approve User");
        approveUserButton.getStyleClass().add("success-button");
        
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.getStyleClass().add("danger-button");

        // Delete user action with loading state and auto-hide
        deleteUserButton.setOnAction(event -> {
            deleteAction.accept(() -> {
                PauseTransition hideLoadingScreen = new PauseTransition(Duration.seconds(3));
                hideLoadingScreen.playFromStart();
                hideLoadingScreen.setOnFinished(e -> userModel.isLoadingProperty().set(false));
            });
        });

        // Approve user action with loading state and auto-hide
        approveUserButton.setOnAction(event -> {
            approveAction.accept(() -> {
                PauseTransition hideLoadingScreen = new PauseTransition(Duration.seconds(3));
                hideLoadingScreen.playFromStart();
                hideLoadingScreen.setOnFinished(e -> userModel.isLoadingProperty().set(false));
            });
        });

        buttonPanel.getChildren().addAll(
            approveUserButton, deleteUserButton
        );
        
        return buttonPanel;
    }
}
