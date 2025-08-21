package com.balazsh.inventory.features.authentication;

import com.balazsh.inventory.domain.model.*;
import com.balazsh.inventory.features.authentication.login.LoginController;
import com.balazsh.inventory.features.authentication.register.RegisterController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

/**
 * Authentication controller coordinating login and registration flows.
 * Handles async authentication operations and manages child controllers.
 */
public class AuthController {

    private final AuthModel authModel;
    private final AuthInteractor authInteractor;
    private final AuthViewBuilder authView;
    private final Runnable switchToDashboard;

    /**
     * Creates authentication controller with bidirectional user state binding.
     * Sets up child controllers and authentication success listener.
     */
    public AuthController(ObjectProperty<ActiveUserDetails> userDetails, Runnable switchToDashboard) {
        this.authModel = new AuthModel();

        LoginController loginController =
                new LoginController(authModel.usernameProperty(),
                        authModel.userLoginDetailsProperty(),
                        authModel.authResultProperty(),
                        authModel.authProgressProperty(),
                        authModel.authenticatedProperty(),
                        this::performLogin,
                        this::switchToRegister);

        RegisterController registerController =
                new RegisterController(authModel.usernameProperty(),
                        this::switchToLogin,
                        this::performRegister,
                        authModel.authResultProperty(),
                        authModel.userRegisterDetailsProperty(),
                        authModel.authProgressProperty());

        this.authView = new AuthViewBuilder(loginController.getView(), registerController.getView(), authModel);
        this.authInteractor = new AuthInteractor(authModel);

        // Bidirectional binding with parent application's user state
        userDetails.bindBidirectional(authModel.loggedInUserProperty());
        this.switchToDashboard = switchToDashboard;
        createLoginSuccessListener();
    }

    /** Sets up listener for automatic dashboard navigation on successful login */
    private void createLoginSuccessListener(){
        authModel.authenticatedProperty().subscribe(authenticated -> {
            if (authenticated) {
                switchToDashboard.run();
            }
        });
    }

    /** Performs login on background thread with progress binding and result handling */
    private void performLogin(Runnable postAsync) {
        authModel.authenticatedProperty().setValue(false);

        Task<Result> loginTask = new Task<>() {
            @Override
            protected Result call() throws Exception {
                return authInteractor.performLogin();
            }
        };

        authModel.authProgressProperty().bind(loginTask.progressProperty());

        loginTask.setOnSucceeded(event -> {
            authInteractor.resolveAuthResult(loginTask.getValue());
            authModel.authProgressProperty().unbind();
            postAsync.run();
        });

        loginTask.setOnFailed(event -> {
            authModel.authProgressProperty().unbind();
            authModel.authResultProperty().set(new Result("failed", "Unexpected error has occurred"));
            postAsync.run();
        });

        Thread loginThread = new Thread(loginTask);
        loginThread.setName("Login Thread");
        loginThread.start();
    }

    /** Switches view to registration form */
    private void switchToRegister(Runnable postGUI){
        authInteractor.switchToRegister();
        postGUI.run();
    }

    /** Switches view to login form */
    private void switchToLogin(Runnable postGUI){
        authInteractor.switchToLogin();
        postGUI.run();
    }

    /** Performs registration on background thread with progress binding and result handling */
    private void performRegister(Runnable postAsync){
        Task<Result> registerTask = new Task<>() {
            @Override
            protected Result call() throws Exception {
                return authInteractor.performRegister();
            }
        };

        authModel.authProgressProperty().bind(registerTask.progressProperty());

        registerTask.setOnSucceeded(event -> {
            authInteractor.resolveAuthResult(registerTask.getValue());
            authModel.authProgressProperty().unbind();
            postAsync.run();
        });
        registerTask.setOnFailed(event -> {
            authModel.authResultProperty().set(new Result("failed", "Unexpected error has occurred"));
            authModel.authProgressProperty().unbind();
            postAsync.run();
        });

        Thread registerThread = new Thread(registerTask);
        registerThread.setName("Register Thread");
        registerThread.start();
    }

    public Region getView(){
       return authView.build();
    }
}
