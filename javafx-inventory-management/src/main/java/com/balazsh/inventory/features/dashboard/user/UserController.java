package com.balazsh.inventory.features.dashboard.user;

import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserEntry;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.beans.binding.Bindings;

import java.util.function.Consumer;

/**
 * User controller managing user account operations and administrative actions.
 * Handles user selection, approval, deletion, and validation workflows.
 */
public class UserController {

    private final UserInteractor userInteractor; // Business logic for user operations
    private final UserViewBuilder userViewBuilder; // UI construction and interaction
    private final UserModel userModel; // User state and selection data
    private final Consumer<Runnable> deleteUserAsync; // Delete selected user
    private final Consumer<Runnable> approveUserAsync;// Approve selected user

    /**
     * Creates user controller with shared state binding and operation callbacks.
     * Sets up bidirectional data binding with parent dashboard for data synchronization.
     */
    public UserController(ObservableList<UserEntry> userEntries,
                          Consumer<Runnable> deleteUserAsync,
                          Consumer<Runnable> approveUserAsync,
                          ObjectProperty<UserEntry> selectedUserEntry,
                          ObjectProperty<Result> resultObjectProperty,
                          DoubleProperty progressProperty) {
        this.userModel = new UserModel();
        this.userInteractor = new UserInteractor(userModel);
        this.userViewBuilder = new UserViewBuilder(userModel, this::deleteUser,this::approveUser);
        
        // Establish bidirectional binding with parent dashboard state
        Bindings.bindContentBidirectional(userModel.getUserEntries(), userEntries);
        this.deleteUserAsync = deleteUserAsync;
        this.approveUserAsync = approveUserAsync;

        userModel.selectedUserEntryProperty().bindBidirectional(selectedUserEntry);
        userModel.progressPropertyProperty().bind(progressProperty);
        userModel.resultPropertyProperty().bind(resultObjectProperty);
    }

    /** Deletes selected user with validation for single user selection */
    private void deleteUser(Runnable onDeleteComplete){
        if (!userInteractor.isSingleUserSelected()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("User deletion warning");
            alert.setContentText("Only one user can be deleted at a time");
            alert.show();
            onDeleteComplete.run();
            return;
        }
        userModel.isLoadingProperty().set(true);
        userInteractor.createSelectedUserEntry();
        deleteUserAsync.accept(onDeleteComplete);
    }

    /** Approves selected user with validation for single user selection */
    private void approveUser(Runnable onApproveComplete){
        if (!userInteractor.isSingleUserSelected()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("User approval warning");
            alert.setContentText("Only one user can be approved at a time");
            alert.show();
            onApproveComplete.run();
            return;
        }
        userModel.isLoadingProperty().set(true);
        userInteractor.createSelectedUserEntry();
        approveUserAsync.accept(onApproveComplete);
    }

    public Region getView() {
        return userViewBuilder.build();
    }
}
