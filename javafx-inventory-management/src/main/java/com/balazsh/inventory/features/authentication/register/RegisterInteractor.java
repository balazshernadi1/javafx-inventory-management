package com.balazsh.inventory.features.authentication.register;

import com.balazsh.inventory.domain.model.AuthResult;
import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserRegisterDetails;
import javafx.scene.control.Alert;

/**
 * Registration business logic handling form validation and registration processing.
 * Enforces password security requirements and validates form completeness.
 */
public class RegisterInteractor {

    private final RegisterModel registerModel;

    public RegisterInteractor (RegisterModel registerModel) {
        this.registerModel = registerModel;
    }

    /** 
     * Validates password against security requirements.
     * Checks length, uppercase, lowercase, digits, and special characters.
     */
    public void isPasswordValid(){
        String password = registerModel.getPassword();
        StringBuilder errors = new StringBuilder();
        boolean isValid = false;

        // Check minimum length requirement
        if (password.length() < 6) {
            errors.append("Password must be at least 6 characters long\n");
        }

        // Check for uppercase letter requirement
        if (!password.matches(".*[A-Z].*")) {
            errors.append("Password must contain at least one uppercase letter (A–Z)\n");
        }

        // Check for lowercase letter requirement
        if (!password.matches(".*[a-z].*")) {
            errors.append("Password must contain at least one lowercase letter (a–z)\n");
        }

        // Check for digit requirement
        if (!password.matches(".*\\d.*")) {
            errors.append("Password must contain at least one digit (0–9)\n");
        }

        // Check for special character requirement
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            errors.append("Password must contain at least one special character (e.g. !@#$%^&*)\n");
        }

        // Set error message or mark as valid
        if (!errors.isEmpty()){
            registerModel.passwordErrorMessageProperty().setValue(errors.toString());
        } else {
            isValid = true;
        }

        registerModel.isPasswordValidProperty().setValue(isValid);
    }

    /** 
     * Validates username length requirement.
     * Ensures username is at least 6 characters long.
     */
    public void isUsernameValid(){
        String username = registerModel.getUsername();
        String error;
        boolean isValid = false;

        if (username.length() < 6) {
            error = "Username must be at least 6 characters long\n";
            registerModel.usernameErrorMessageProperty().setValue(error);
        } else {
            isValid = true;
        }
        registerModel.isUsernameValidProperty().setValue(isValid);
    }

    /** 
     * Validates complete registration form before submission.
     * Checks username validity, password validity, and role selection.
     */
    public boolean preRegisterValidation(){
        if (!registerModel.isIsPasswordValid() || !registerModel.isIsUsernameValid() || registerModel.getRole().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Register form error");
            alert.setContentText("Please fill in all the required fields");
            alert.show();
            return false;
        }
        return true;
    }

    /** Cleans up form state after registration completion */
    public void postRegister(){
        registerModel.isLoadingProperty().setValue(false);
        registerModel.usernameProperty().setValue("");
        registerModel.passwordProperty().setValue("");
    }

    /** Creates registration request object from form data for authentication service */
    public void createRegisterDetails() {
        registerModel.userRegisterDetailsProperty().set(
            new UserRegisterDetails(
                registerModel.getUsername(), 
                registerModel.getPassword(), 
                registerModel.getRole()
            )
        );
    }
}
