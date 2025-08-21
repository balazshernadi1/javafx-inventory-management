package com.balazsh.inventory.features.dashboard.user;

import com.balazsh.inventory.domain.model.UserEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserInteractorTest {

    private UserModel userModel;
    private UserInteractor userInteractor;

    @BeforeEach
    void setUp() {
        userModel = new UserModel();
        userInteractor = new UserInteractor(userModel);
    }

    @Test
    void isSingleUserSelected_ShouldReturnTrue_WhenExactlyOneUserIsSelected() {
        // Given
        UserEntry user1 = new UserEntry(1, "user1", "admin", "pending");
        UserEntry user2 = new UserEntry(2, "user2", "user", "pending");
        UserEntry user3 = new UserEntry(3, "user3", "user", "pending");
        
        user1.setUserSelected(false);
        user2.setUserSelected(true);  // Only this one is selected
        user3.setUserSelected(false);
        
        userModel.getUserEntries().addAll(user1, user2, user3);

        // When
        boolean result = userInteractor.isSingleUserSelected();

        // Then
        assertTrue(result);
    }

    @Test
    void isSingleUserSelected_ShouldReturnFalse_WhenNoUserIsSelected() {
        // Given
        UserEntry user1 = new UserEntry(1, "user1", "admin", "pending");
        UserEntry user2 = new UserEntry(2, "user2", "user", "pending");
        
        user1.setUserSelected(false);
        user2.setUserSelected(false);
        
        userModel.getUserEntries().addAll(user1, user2);

        // When
        boolean result = userInteractor.isSingleUserSelected();

        // Then
        assertFalse(result);
    }

    @Test
    void isSingleUserSelected_ShouldReturnFalse_WhenMultipleUsersAreSelected() {
        // Given
        UserEntry user1 = new UserEntry(1, "user1", "admin", "pending");
        UserEntry user2 = new UserEntry(2, "user2", "user", "pending");
        UserEntry user3 = new UserEntry(3, "user3", "user", "pending");
        
        user1.setUserSelected(true);
        user2.setUserSelected(true);  // Two users selected
        user3.setUserSelected(false);
        
        userModel.getUserEntries().addAll(user1, user2, user3);

        // When
        boolean result = userInteractor.isSingleUserSelected();

        // Then
        assertFalse(result);
    }

    @Test
    void isSingleUserSelected_ShouldReturnFalse_WhenUserListIsEmpty() {
        // Given - empty user list

        // When
        boolean result = userInteractor.isSingleUserSelected();

        // Then
        assertFalse(result);
    }

    @Test
    void isSingleUserSelected_ShouldReturnTrue_WhenOnlyOneUserExistsAndIsSelected() {
        // Given
        UserEntry user1 = new UserEntry(1, "user1", "admin", "pending");
        user1.setUserSelected(true);
        
        userModel.getUserEntries().add(user1);

        // When
        boolean result = userInteractor.isSingleUserSelected();

        // Then
        assertTrue(result);
    }
}