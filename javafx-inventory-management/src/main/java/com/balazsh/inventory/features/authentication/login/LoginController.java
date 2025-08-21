package com.balazsh.inventory.features.authentication.login;

import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserLoginDetails;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

/**
 * Login controller handling login form interactions and validation.
 * Coordinates between login model/interactor and parent authentication controller.
 */
public class LoginController {

    private final LoginModel loginModel;
    private final LoginInteractor loginInteractor;
    private final LoginViewBuilder loginViewBuilder;
    private final Consumer<Runnable> performLogin;        // Callback to parent for async login
    private final Consumer<Runnable> goToRegister;       // Callback to switch to registration

    /**
     * Creates login controller with shared state binding to parent auth controller.
     * Sets up bidirectional binding for data synchronization.
     */
    public LoginController(StringProperty username,
                           ObjectProperty<UserLoginDetails> userLoginDetails,
                           ObjectProperty<Result> authResult,
                           DoubleProperty progress,
                           BooleanProperty authenticated, Consumer<Runnable> performLogin,
                           Consumer<Runnable> goToRegister
    ){
        this.loginModel = new LoginModel();
        this.loginInteractor = new LoginInteractor(loginModel);
        this.loginViewBuilder = new LoginViewBuilder(loginModel,
                this::loginAction,
                loginInteractor::resolveLoginResult,
                goToRegister);
        this.performLogin = performLogin;
        this.goToRegister = goToRegister;

        // Bind shared state with parent authentication controller
        loginModel.usernameProperty().bindBidirectional(username);
        userLoginDetails.bind(loginModel.userLoginDetailsProperty());
        loginModel.resultProperty().bindBidirectional(authResult);
        loginModel.progressProperty().bind(progress);
        loginModel.authenticatedProperty().bindBidirectional(authenticated);
    }

    /** 
     * Handles login button click with validation and request creation.
     * Delegates to parent controller for actual authentication.
     */
    private void loginAction(Runnable onLoginComplete) {
        if (!loginInteractor.preLoginValidation()) {
            onLoginComplete.run();
            return;
        }

        loginInteractor.preLogin();
        loginInteractor.createLoginRequest();
        performLogin.accept(onLoginComplete);
    }

    public Region getView(){
        return loginViewBuilder.build();
    }
}
