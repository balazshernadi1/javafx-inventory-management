package com.balazsh.inventory.features.dashboard.user;

import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserEntry;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserModel {

    // User data and selection
    private final ObservableList<UserEntry> userEntries = FXCollections.observableArrayList();  // User account list
    private final ObjectProperty<UserEntry> selectedUserEntry = new SimpleObjectProperty<>();   // Currently selected user
    
    // Operation state and feedback
    private final ObjectProperty<Result> resultProperty = new SimpleObjectProperty<>();         // Operation results
    private final DoubleProperty progressProperty = new SimpleDoubleProperty();                 // Progress tracking
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);                 // Loading state

    public BooleanProperty isLoadingProperty() {
        return isLoading;
    }

    public double getProgressProperty() {
        return progressProperty.get();
    }

    public DoubleProperty progressPropertyProperty() {
        return progressProperty;
    }

    public Result getResultProperty() {
        return resultProperty.get();
    }

    public ObjectProperty<Result> resultPropertyProperty() {
        return resultProperty;
    }

    public UserEntry getSelectedUserEntry() {
        return selectedUserEntry.get();
    }

    public ObjectProperty<UserEntry> selectedUserEntryProperty() {
        return selectedUserEntry;
    }

    public ObservableList<UserEntry> getUserEntries() {
        return userEntries;
    }
}
