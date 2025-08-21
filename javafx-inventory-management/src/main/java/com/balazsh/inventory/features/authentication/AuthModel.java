package com.balazsh.inventory.features.authentication;

import com.balazsh.inventory.domain.model.*;
import javafx.beans.property.*;

/**
 * Authentication model managing user session state, form data, and view switching.
 * All properties are observable for reactive UI binding.
 */
public class AuthModel {

    private final StringProperty username = new SimpleStringProperty(); // Shared username across forms
    private final BooleanProperty showLogin = new SimpleBooleanProperty(true); // Show login form
    private final BooleanProperty showRegister = new SimpleBooleanProperty(false); // Show register form
    private final ObjectProperty<ActiveUserDetails> loggedInUser = new SimpleObjectProperty<>(); // Current user session
    private final ObjectProperty<UserLoginDetails> userLoginDetails = new SimpleObjectProperty<>();  // Login form data
    private final ObjectProperty<UserRegisterDetails> userRegisterDetails = new SimpleObjectProperty<>();  // Registration form data
    private final BooleanProperty authenticated = new SimpleBooleanProperty(false);  // Login success state
    private final BooleanProperty notAuthenticated = new SimpleBooleanProperty(true); // Inverse of authenticated
    private final ObjectProperty<Result> authResult = new SimpleObjectProperty<>(new Result("","")); // Auth operation result
    private final DoubleProperty authProgress = new SimpleDoubleProperty(); // Progress for async operations

    public AuthModel() {

    }

    public UserRegisterDetails getUserRegisterDetails() {
        return userRegisterDetails.get();
    }

    public ObjectProperty<UserRegisterDetails> userRegisterDetailsProperty() {
        return userRegisterDetails;
    }

    public boolean isAuthenticated() {
        return authenticated.get();
    }

    public BooleanProperty authenticatedProperty() {
        return authenticated;
    }

    public double getAuthProgress() {
        return authProgress.get();
    }

    public DoubleProperty authProgressProperty() {
        return authProgress;
    }

    public UserLoginDetails getUserLoginDetails() {
        return userLoginDetails.get();
    }

    public ObjectProperty<UserLoginDetails> userLoginDetailsProperty() {
        return userLoginDetails;
    }

    public Result getAuthResult() {
        return authResult.get();
    }

    public ObjectProperty<Result> authResultProperty() {
        return authResult;
    }

    public boolean isNotAuthenticated() {
        return notAuthenticated.get();
    }

    public boolean getNotAuthenticated() {
        return notAuthenticated.get();
    }

    public BooleanProperty notAuthenticatedProperty() {
        return notAuthenticated;
    }

    public ActiveUserDetails getLoggedInUser() {
        return loggedInUser.get();
    }

    public ObjectProperty<ActiveUserDetails> loggedInUserProperty() {
        return loggedInUser;
    }

    public boolean isShowLogin() {
        return showLogin.get();
    }

    public BooleanProperty showLoginProperty() {
        return showLogin;
    }

    public boolean isShowRegister() {
        return showRegister.get();
    }

    public BooleanProperty showRegisterProperty() {
        return showRegister;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }
}
