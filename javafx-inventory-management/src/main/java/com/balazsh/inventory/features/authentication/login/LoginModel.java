package com.balazsh.inventory.features.authentication.login;

import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserLoginDetails;
import javafx.beans.property.*;

/**
 * Login model managing form state, validation errors, and authentication progress.
 * Tracks login attempts and provides observable properties for UI binding.
 */
public class LoginModel {
    private final SimpleStringProperty username = new SimpleStringProperty(""); // Username input
    private final SimpleStringProperty password = new SimpleStringProperty("");         // Password input
    private final SimpleStringProperty usernameErrorMessage = new SimpleStringProperty(); // Username validation errors
    private final SimpleStringProperty passwordErrorMessage = new SimpleStringProperty(); // Password validation errors
    private final BooleanProperty usernameError = new SimpleBooleanProperty(false); // Username has error state
    private final BooleanProperty passwordError = new SimpleBooleanProperty(false); // Password has error state
    private final IntegerProperty loginAttempts = new SimpleIntegerProperty(0); // Failed login attempt counter
    private final BooleanProperty isLoggedIn = new SimpleBooleanProperty(false); // Local login state
    private final BooleanProperty authenticated = new SimpleBooleanProperty(false); // Shared auth state with parent
    private final BooleanProperty loading = new SimpleBooleanProperty(false); // Loading screen visibility
    private final DoubleProperty progress = new SimpleDoubleProperty(0);  // Authentication progress
    private final ObjectProperty<Result> result = new SimpleObjectProperty<>(); // Authentication result
    private final ObjectProperty<UserLoginDetails> userLoginDetails = new SimpleObjectProperty<>();  // Login request data

    public LoginModel() {

    }

    public boolean isAuthenticated() {
        return authenticated.get();
    }

    public BooleanProperty authenticatedProperty() {
        return authenticated;
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public UserLoginDetails getUserLoginDetails() {
        return userLoginDetails.get();
    }

    public ObjectProperty<UserLoginDetails> userLoginDetailsProperty() {
        return userLoginDetails;
    }

    public Result getResult() {
        return result.get();
    }

    public ObjectProperty<Result> resultProperty() {
        return result;
    }

    public boolean isLoading() {
        return loading.get();
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public String getUsernameErrorMessage() {
        return usernameErrorMessage.get();
    }

    public SimpleStringProperty usernameErrorMessageProperty() {
        return usernameErrorMessage;
    }

    public String getPasswordErrorMessage() {
        return passwordErrorMessage.get();
    }

    public SimpleStringProperty passwordErrorMessageProperty() {
        return passwordErrorMessage;
    }

    public boolean isIsLoggedIn() {
        return isLoggedIn.get();
    }

    public BooleanProperty isLoggedInProperty() {
        return isLoggedIn;
    }

    public boolean isUsernameError() {
        return usernameError.get();
    }

    public BooleanProperty usernameErrorProperty() {
        return usernameError;
    }

    public boolean isPasswordError() {
        return passwordError.get();
    }

    public BooleanProperty passwordErrorProperty() {
        return passwordError;
    }

    public int getLoginAttempts() {
        return loginAttempts.get();
    }

    public IntegerProperty loginAttemptsProperty() {
        return loginAttempts;
    }
}
