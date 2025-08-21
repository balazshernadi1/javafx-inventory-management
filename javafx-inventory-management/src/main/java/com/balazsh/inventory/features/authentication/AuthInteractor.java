package com.balazsh.inventory.features.authentication;

import com.balazsh.inventory.dao.UserDaoImpl;
import com.balazsh.inventory.domain.model.*;
import com.balazsh.inventory.domain.service.AuthService;
import com.balazsh.inventory.util.exceptions.AuthenticationException;
import javafx.application.Platform;

/**
 * Authentication business logic layer handling login, registration, and view switching.
 * Integrates with AuthService and manages thread-safe UI updates.
 */
public class AuthInteractor {

    private final AuthModel authModel;

    public AuthInteractor(AuthModel authModel) {
        this.authModel = authModel;
    }

    /** Sets authentication result in model for UI feedback */
    public void resolveAuthResult(Result result){
        authModel.authResultProperty().set(result);
    }

    /** 
     * Performs login authentication with comprehensive error handling.
     * Updates user details on UI thread after successful authentication.
     */
    public Result performLogin() {
        AuthService service = new AuthService(new UserDaoImpl());
        UserLoginDetails userLoginDetails = authModel.getUserLoginDetails();
        try {
            ActiveUserDetails userDetails = service.login(userLoginDetails.username(), userLoginDetails.password());
            // Thread-safe UI update
            Platform.runLater(() -> {
                authModel.loggedInUserProperty().set(userDetails);
            });
            return new Result("success", "Login Successful");
        } catch (AuthenticationException e) {
            return new Result("failure", e.getMessage());
        } catch (Exception e) {
            return new Result("failure", "Unexpected error has occurred");
        }
    }

    /** 
     * Performs user registration with error handling.
     * Creates new user account but doesn't automatically log them in.
     */
    public Result performRegister(){
        AuthService service = new AuthService(new UserDaoImpl());
        try {
            UserRegisterDetails details = authModel.getUserRegisterDetails();
            service.register(details.username(), details.password(), details.role());
            return new Result("success", "Successful register");
        } catch (AuthenticationException e) {
            return new Result("failed", e.getMessage());
        } catch (Exception e) {
            return new Result("failed", "An unexpected error has occurred");
        }
    }

    /** Switches view to show login form */
    public void switchToLogin(){
        authModel.showLoginProperty().set(true);
        authModel.showRegisterProperty().set(false);
    }

    /** Switches view to show registration form */
    public void switchToRegister(){
        authModel.showLoginProperty().set(false);
        authModel.showRegisterProperty().set(true);
    }
}
