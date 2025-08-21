package com.balazsh.inventory.features.authentication.register;

import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserLoginDetails;
import com.balazsh.inventory.domain.model.UserRegisterDetails;
import javafx.beans.property.*;

/**
 * Registration model managing form state, validation results, and registration progress.
 * Tracks field validity and provides observable properties for UI binding.
 */
public class RegisterModel {

    private final StringProperty username = new SimpleStringProperty(); // Username input
    private final StringProperty password = new SimpleStringProperty(); // Password input
    private final StringProperty role = new SimpleStringProperty();  // Selected user role
    private final StringProperty passwordErrorMessage =  new SimpleStringProperty(); // Password validation errors
    private final StringProperty usernameErrorMessage =  new SimpleStringProperty(); // Username validation errors
    private final BooleanProperty isPasswordValid = new SimpleBooleanProperty(); // Password meets requirements
    private final BooleanProperty isUsernameValid = new SimpleBooleanProperty();  // Username meets requirements
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false); // Loading screen visibility
    private final ObjectProperty<UserRegisterDetails> userRegisterDetails = new SimpleObjectProperty<>(); // Registration request data
    private final ObjectProperty<Result> result = new SimpleObjectProperty<>(new Result("", "")); // Registration result
    private final DoubleProperty registerProgress = new SimpleDoubleProperty(); // Registration progress

    public double getRegisterProgress() { return registerProgress.get(); }
    public DoubleProperty registerProgressProperty() { return registerProgress; }
    public UserRegisterDetails getUserRegisterDetails() { return userRegisterDetails.get(); }
    public ObjectProperty<UserRegisterDetails> userRegisterDetailsProperty() { return userRegisterDetails; }
    public Result getResult() { return result.get(); }
    public ObjectProperty<Result> resultProperty() { return result; }
    public boolean isIsLoading() { return isLoading.get(); }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public boolean isIsPasswordValid() { return isPasswordValid.get(); }
    public BooleanProperty isPasswordValidProperty() { return isPasswordValid; }
    public boolean isIsUsernameValid() { return isUsernameValid.get(); }
    public BooleanProperty isUsernameValidProperty() { return isUsernameValid; }
    public String getPasswordErrorMessage() { return passwordErrorMessage.get(); }
    public StringProperty passwordErrorMessageProperty() { return passwordErrorMessage; }
    public String getUsernameErrorMessage() { return usernameErrorMessage.get(); }
    public StringProperty usernameErrorMessageProperty() { return usernameErrorMessage; }
    public String getPassword() { return password.get(); }
    public StringProperty passwordProperty() { return password; }
    public String getRole() { return role.get(); }
    public StringProperty roleProperty() { return role; }
    public String getUsername() { return username.get(); }
    public StringProperty usernameProperty() { return username; }
}
