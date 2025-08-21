package com.balazsh.inventory.features.authentication.login;

import com.balazsh.inventory.domain.model.AuthResult;
import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserLoginDetails;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Login business logic handling validation, login attempts, and result processing.
 * Manages form validation and creates login requests for authentication.
 */
public class LoginInteractor {

    private final LoginModel loginModel;

    public LoginInteractor(LoginModel loginModel) {
        this.loginModel = loginModel;
    }

    /** 
     * Validates login form and checks attempt limits.
     * Shows warning dialog after 3 failed attempts and validates required fields.
     */
    public boolean preLoginValidation() {
        boolean isValid = true;
        
        // Check login attempt limit
        if (loginModel.getLoginAttempts() == 3){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login Warning");
            alert.setContentText("Maximum number of attempts reached");
            isValid = false;
        }
        
        // Validate username field
        if (loginModel.getUsername().isBlank() || loginModel.getUsername().isEmpty()) {
            loginModel.usernameErrorMessageProperty().setValue("Username field cannot be empty");
            isValid = false;
        }
        
        // Validate password field
        if (loginModel.getPassword().isBlank() || loginModel.getPassword().isEmpty()) {
            loginModel.passwordErrorMessageProperty().setValue("Password field cannot be empty");
            isValid = false;
        }

        return isValid;
    }

    /** Prepares model state for login attempt by clearing previous results and showing loading */
    public void preLogin(){
        if (loginModel.resultProperty().get() != null) {
            loginModel.resultProperty().get().messageProperty().set("");
        }
        loginModel.loadingProperty().set(true);
    }

    /** 
     * Processes login result by updating attempt counter and authentication state.
     * Increments attempts on failure, resets on success.
     */
    public void resolveLoginResult(){
        Result loginResult = loginModel.getResult();
        if (loginResult.getStatus().equals("failure")) {
            loginModel.loginAttemptsProperty().set(loginModel.getLoginAttempts()+1);
        } else if (loginResult.getStatus().equals("success")) {
            loginModel.loginAttemptsProperty().set(0);
            loginModel.authenticatedProperty().set(true);
        }
    }

    /** Creates login request object from form data for authentication service */
    public void createLoginRequest() {
        loginModel.userLoginDetailsProperty().set(
            new UserLoginDetails(loginModel.getUsername(), loginModel.getPassword())
        );
    }
}
