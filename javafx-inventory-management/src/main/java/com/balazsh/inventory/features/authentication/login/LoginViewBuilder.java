package com.balazsh.inventory.features.authentication.login;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * Login view builder creating the login form UI with loading overlay and error animations.
 * Uses StackPane to overlay main content with loading screen for smooth transitions.
 */
public class LoginViewBuilder implements Builder<Region> {

    private final LoginModel model;
    private TextField usernameField;
    private PasswordField passwordField;
    private final Consumer<Runnable> loginAction; // Login button action callback
    private final Runnable onLoginComplete; // Post-login completion callback
    private final Consumer<Runnable> goToRegister; // Switch to register callback

    public LoginViewBuilder(LoginModel model, Consumer<Runnable> loginAction, Runnable onLoginComplete, Consumer<Runnable> goToRegister) {
        this.model = model;
        this.loginAction = loginAction;
        this.onLoginComplete = onLoginComplete;
        this.goToRegister = goToRegister;
    }

    /** Builds login view with overlay structure for main content and loading screen */
    @Override
    public Region build() {
        StackPane root = new StackPane();
        root.getStylesheets().add(LoginViewBuilder.class.getResource("/auth_page.css").toExternalForm());
        root.getChildren().addAll(createMainContent(), createLoadingScreen());
        return root;
    }

    /** Creates loading overlay with progress indicator and dynamic status messages */
    private Node createLoadingScreen() {
        BorderPane loadingScreenRoot = new BorderPane();
        loadingScreenRoot.visibleProperty().bind(model.loadingProperty());

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        Label loadingLabel = new Label("Trying to log in...");
        loadingLabel.getStyleClass().add("loading-message");

        // Bind status message to authentication result
        Label statusLabel = new Label();
        statusLabel.textProperty().bind(
                Bindings.selectString(model.resultProperty(), "message")
        );

        // Progress bar with dynamic color based on result
        ProgressBar progressIndicator = new ProgressBar();
        progressIndicator.progressProperty().bind(model.progressProperty());
        progressIndicator.setStyle("-fx-progress-color: green;");

        // Update progress bar appearance based on authentication result
        model.resultProperty().subscribe(result -> {
            if (result.getStatus().equals("failure")) {
                progressIndicator.progressProperty().unbind();
                progressIndicator.setStyle("-fx-progress-color: red;");
                progressIndicator.setProgress(1.0);
            } else {
                progressIndicator.progressProperty().unbind();
                progressIndicator.setProgress(0);
                progressIndicator.progressProperty().bind(model.progressProperty());
                progressIndicator.setStyle("-fx-accent: green;");
            }
        });

        vbox.getChildren().addAll(loadingLabel, statusLabel, progressIndicator);
        loadingScreenRoot.setCenter(vbox);
        return loadingScreenRoot;
    }

    /** Creates main content area with header and form, disabled during loading */
    private Node createMainContent(){
        VBox mainContentRoot = new VBox();
        mainContentRoot.setAlignment(Pos.CENTER);
        mainContentRoot.setSpacing(20);
        mainContentRoot.setPadding(new Insets(60));
        mainContentRoot.getChildren().addAll(createHeaderContent(), createLoginFormContent());
        mainContentRoot.disableProperty().bind(model.loadingProperty());
        return mainContentRoot;
    }

    /** Creates welcome header with title and subtitle */
    private Node createHeaderContent(){
        Label welcomeLabel = new Label("Welcome to the Inventory Management System");
        welcomeLabel.getStyleClass().add("main-header");

        Label subHeaderLabel = new Label("Please enter your username and password");
        subHeaderLabel.getStyleClass().add("main-subheader");

        VBox vBox = new VBox(welcomeLabel, subHeaderLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        return vBox;
    }

    /** Creates login form container with username, password, and button sections */
    private Node createLoginFormContent(){
        VBox loginFormHolder = new VBox(createUsernameBox(), createPasswordBox(), createButtonBox());
        loginFormHolder.setAlignment(Pos.CENTER);
        loginFormHolder.setSpacing(10);
        loginFormHolder.getStyleClass().add("form-container");
        return loginFormHolder;
    }

    /** Creates username input field with label and animated error display */
    private Node createUsernameBox(){
        usernameField = new TextField();
        usernameField.getStyleClass().add("form-field");
        model.usernameProperty().bindBidirectional(usernameField.textProperty());

        Label usernameLabel = new Label("Username");
        usernameLabel.getStyleClass().add("text-field-label");

        Label usernameError = new Label();
        usernameError.getStyleClass().add("text-field-error");
        addErrorAnimation(usernameError, model.usernameErrorMessageProperty());

        VBox usernameBoxGroup = new VBox(usernameLabel, usernameField, usernameError);
        VBox.setMargin(usernameBoxGroup, new Insets(0, 0, 15,0));
        return usernameBoxGroup;
    }

    /** 
     * Adds animated error display to labels with auto-hide after 3 seconds.
     * Binds label visibility to error message presence.
     */
    private void addErrorAnimation(Label label, StringProperty stringProperty){
        label.textProperty().bind(stringProperty);
        label.visibleProperty().bind(Bindings.isNotEmpty(stringProperty));
        label.managedProperty().bind(Bindings.isNotEmpty(stringProperty));

        PauseTransition hideLabel = new PauseTransition(Duration.seconds(3));
        hideLabel.setOnFinished(event -> {
            stringProperty.set(null);
        });

        stringProperty.subscribe((s)->{
            hideLabel.playFromStart();
        });
    }

    /** Creates password input field with label and animated error display */
    private Node createPasswordBox() {
        passwordField = new PasswordField();
        passwordField.getStyleClass().add("form-field");
        model.passwordProperty().bindBidirectional(passwordField.textProperty());

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("text-field-label");

        Label passwordError = new Label();
        passwordError.getStyleClass().add("text-field-error");
        addErrorAnimation(passwordError, model.passwordErrorMessageProperty());

        VBox passwordBoxGroup = new VBox(passwordLabel, passwordField, passwordError);
        VBox.setMargin(passwordBoxGroup, new Insets(0, 0, 15,0));
        return passwordBoxGroup;
    }

    /** 
     * Creates login button and register link with action handlers.
     * Disables buttons during operations and provides timed loading screen.
     */
    private Node createButtonBox(){
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("form-button");

        Hyperlink registerLink = new Hyperlink("Register");
        registerLink.getStyleClass().add("form-link");

        // Login button action with button state management
        loginButton.setOnAction(event -> {
            loginButton.setDisable(true);
            registerLink.setDisable(true);
            loginAction.accept(() -> {
                loginButton.setDisable(false);
                registerLink.setDisable(false);

                // Auto-hide loading screen after 3 seconds
                PauseTransition hideLogin = new PauseTransition(Duration.seconds(3));
                hideLogin.playFromStart();

                hideLogin.setOnFinished(event1 ->{
                    model.loadingProperty().set(false);
                    onLoginComplete.run();
                });
            });
        });

        // Register link action with button state management
        registerLink.setOnAction(event -> {
            loginButton.setDisable(true);
            registerLink.setDisable(true);
            goToRegister.accept(()->{
                loginButton.setDisable(false);
                registerLink.setDisable(false);
            });
        });

        VBox buttonBoxGroup = new VBox(loginButton, registerLink);
        buttonBoxGroup.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonBoxGroup, new Insets(20,0,0,0));
        buttonBoxGroup.setSpacing(15);
        return buttonBoxGroup;
    }
}
