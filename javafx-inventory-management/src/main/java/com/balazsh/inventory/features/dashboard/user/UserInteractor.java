package com.balazsh.inventory.features.dashboard.user;

import com.balazsh.inventory.dao.UserDaoImpl;
import com.balazsh.inventory.domain.model.ProductEntry;
import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserEntry;
import com.balazsh.inventory.domain.service.UserService;
import javafx.scene.control.Alert;

/**
 * User business logic handling user management operations and selection validation.
 * Manages user selection state and prepares data for administrative actions.
 */
public class UserInteractor {

    private final UserModel userModel;

    public UserInteractor(UserModel userModel) {
        this.userModel = userModel;
    }

    /** Sets the first selected user as the active user for administrative operations */
    public void createSelectedUserEntry(){
        UserEntry selectedUserEntry = userModel.getUserEntries()
                .stream()
                .filter(UserEntry::isUserSelected)
                .findFirst().get();  // Safe due to validation in controller
        userModel.selectedUserEntryProperty().set(selectedUserEntry);
    }

    /** 
     * Validates that exactly one user is selected for administrative actions.
     * Enforces single-user operations for approval and deletion.
     */
    public boolean isSingleUserSelected() {
        long count = userModel.getUserEntries().stream()
                .filter(UserEntry::isUserSelected)
                .limit(2)  // Only check up to 2 for efficiency
                .count();
        return count == 1;
    }
}
