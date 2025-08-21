package com.balazsh.inventory.features.authentication.register;

import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserRegisterDetails;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

/**
 * Registration controller handling user registration form and validation.
 * Coordinates between registration model/interactor and parent authentication controller.
 */
public class RegisterController {

    private final RegisterInteractor registerInteractor;
    private final RegisterViewBuilder registerViewBuilder;
    private final RegisterModel registerModel;
    private final Consumer<Runnable> switchToLogin; // Callback to switch to login view
    private final Consumer<Runnable> performRegister; // Callback to parent for async registration

    /**
     * Creates registration controller with shared state binding to parent auth controller.
     * Sets up bidirectional binding for data synchronization and validation callbacks.
     */
    public RegisterController(StringProperty username,
                              Consumer<Runnable> switchToLogin,
                              Consumer<Runnable> performRegister,
                              ObjectProperty<Result> resultObjectProperty,
                              ObjectProperty<UserRegisterDetails> userRegisterDetailsObjectProperty,
                              DoubleProperty registerProgressProperty) {

        this.registerModel = new RegisterModel();
        this.registerInteractor = new RegisterInteractor(registerModel);
        this.registerViewBuilder =
                new RegisterViewBuilder(registerModel,
                        this::switchToLoginView,
                        this::handleRegister,
                        this::validatePassword,
                        this::validateUsername,
                        registerInteractor::postRegister);
        
        // Bind shared state with parent authentication controller
        registerModel.usernameProperty().bindBidirectional(username);
        this.switchToLogin = switchToLogin;
        this.performRegister = performRegister;

        resultObjectProperty.bindBidirectional(registerModel.resultProperty());
        userRegisterDetailsObjectProperty.bind(registerModel.userRegisterDetailsProperty());
        registerModel.registerProgressProperty().bind(registerProgressProperty);
    }

    /** Switches to login view and clears password for security */
    public void switchToLoginView(Runnable onComplete) {
        registerModel.passwordProperty().setValue("");
        switchToLogin.accept(onComplete);
    }

    /** Triggers password validation through interactor */
    public void validatePassword(){
        registerInteractor.isPasswordValid();
    }

    /** Triggers username validation through interactor */
    public void validateUsername(){
        registerInteractor.isUsernameValid();
    }

    /** 
     * Handles registration button click with validation and request creation.
     * Delegates to parent controller for actual registration.
     */
    public void handleRegister(Runnable onRegisterComplete) {
        if (!registerInteractor.preRegisterValidation()){
            onRegisterComplete.run();
            return;
        }

        registerModel.isLoadingProperty().set(true);
        registerInteractor.createRegisterDetails();
        performRegister.accept(onRegisterComplete);
    }

    public Region getView(){
        return registerViewBuilder.build();
    }
}
