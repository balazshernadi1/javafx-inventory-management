package com.balazsh.inventory.features.authentication.register;

import com.balazsh.inventory.domain.model.AuthResult;
import com.balazsh.inventory.util.enums.ROLE;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import javafx.util.Duration;

import java.net.URL;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Registration view builder creating the registration form UI with real-time validation.
 * Features role selection, visual validation feedback, and loading overlay.
 */
public class RegisterViewBuilder implements Builder<Region> {

    private final RegisterModel registerModel;
    private final Consumer<Runnable> switchToLoginView; // Switch to login view callback
    private final Consumer<Runnable> handleRegister; // Registration action callback
    private final Runnable checkPasswordValidity; // Real-time password validation
    private final Runnable checkUsernameValidity; // Real-time username validation
    private final Runnable onRegisterComplete; // Post-registration callback
    
    // Form components
    private TextField usernameField;
    private PasswordField passwordField;
    private ComboBox<String> roleComboBox;
    
    // CSS pseudo-classes for visual validation
    private static final PseudoClass VALID = PseudoClass.getPseudoClass("valid-field");
    private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid-field");

    public RegisterViewBuilder(RegisterModel registerModel,
                               Consumer<Runnable> switchToLoginView,
                               Consumer<Runnable> handleRegister,
                               Runnable checkPasswordValidity, Runnable checkUsernameValidity,
                               Runnable onRegisterComplete) {
        this.switchToLoginView = switchToLoginView;
        this.handleRegister = handleRegister;
        this.registerModel = registerModel;
        this.checkPasswordValidity = checkPasswordValidity;
        this.checkUsernameValidity = checkUsernameValidity;
        this.onRegisterComplete = onRegisterComplete;
    }

    /** Builds registration view with overlay structure for main content and loading screen */
    @Override
    public Region build() {
        StackPane root = new StackPane();
        root.getStylesheets().add(RegisterViewBuilder.class.getResource("/auth_page.css").toExternalForm());
        root.getChildren().addAll(createMainContent(), createLoadingScreen());
        return root;
    }

    /** Creates loading overlay with progress indicator and dynamic status messages */
    private Node createLoadingScreen() {
        BorderPane loadingScreenRoot = new BorderPane();
        loadingScreenRoot.visibleProperty().bind(registerModel.isLoadingProperty());

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        Label loadingLabel = new Label("Trying to log in...");
        loadingLabel.getStyleClass().add("loading-message");

        // Bind status message to registration result message
        Label statusLabel = new Label();
        statusLabel.textProperty().bind(
                Bindings.selectString(registerModel.resultProperty(), "message")
        );

        // Progress bar with dynamic color based on result
        ProgressBar progressIndicator = new ProgressBar();
        progressIndicator.progressProperty().bind(registerModel.registerProgressProperty());
        progressIndicator.setStyle("-fx-progress-color: green;");

        // Update progress bar appearance based on registration result
        registerModel.resultProperty().subscribe(result -> {
            if (result.getStatus().equals("failure")) {
                progressIndicator.progressProperty().unbind();
                progressIndicator.setStyle("-fx-progress-color: red;");
                progressIndicator.setProgress(1.0);
            } else {
                progressIndicator.progressProperty().unbind();
                progressIndicator.progressProperty().bind(registerModel.registerProgressProperty());
                progressIndicator.setStyle("-fx-accent: green;");
            }
        });

        vbox.getChildren().addAll(loadingLabel, statusLabel, progressIndicator);
        loadingScreenRoot.setCenter(vbox);
        return loadingScreenRoot;
    }

    /** Creates main content area with header and form, disabled during loading */
    private Node createMainContent() {
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(60));
        mainContainer.disableProperty().bind(registerModel.isLoadingProperty());
        mainContainer.getChildren().addAll(createHeaderContent(), createRegisterFormContent());
        return mainContainer;
    }

    /** Creates registration header with title and subtitle */
    private Node createHeaderContent() {
        Label welcomeLabel = new Label("Register an account");
        welcomeLabel.getStyleClass().add("main-header");

        Label subHeaderLabel = new Label("Please fill out the following form");
        subHeaderLabel.getStyleClass().add("main-subheader");
        VBox vBox = new VBox(welcomeLabel, subHeaderLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        return vBox;
    }

    /** Creates registration form container with username, password, role, and button sections */
    private Node createRegisterFormContent() {
        VBox registerFormBox = new VBox(createUsernameBox(), createPasswordBox(), createRoleSelectionBox() ,createButtonBox());
        registerFormBox.setAlignment(Pos.CENTER);
        registerFormBox.setSpacing(10);
        registerFormBox.getStyleClass().add("form-container");
        return registerFormBox;
    }

    /** 
     * Creates username input field with real-time validation and visual feedback.
     * Updates CSS pseudo-classes based on validation state.
     */
    private Node createUsernameBox() {
        usernameField = new TextField();
        usernameField.getStyleClass().add("form-field");
        registerModel.usernameProperty().bindBidirectional(usernameField.textProperty());

        // Real-time validation with visual feedback
        usernameField.textProperty().subscribe(()->{
                    checkUsernameValidity.run();
                    usernameField.pseudoClassStateChanged(VALID, registerModel.isIsUsernameValid());
                    usernameField.pseudoClassStateChanged(INVALID, !registerModel.isIsUsernameValid());
                });

        Label usernameLabel = new Label("Username");
        usernameLabel.getStyleClass().add("text-field-label");

        Label usernameError = new Label();
        usernameError.getStyleClass().add("text-field-error");
        addErrorAnimation(usernameError, registerModel.usernameErrorMessageProperty());

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

    /** 
     * Creates password input field with real-time validation and visual feedback.
     * Updates CSS pseudo-classes based on security requirements validation.
     */
    private Node createPasswordBox() {
        passwordField = new PasswordField();
        passwordField.getStyleClass().add("form-field");
        registerModel.passwordProperty().bindBidirectional(passwordField.textProperty());

        // Real-time validation with visual feedback
        passwordField.textProperty().subscribe(()->{
            checkPasswordValidity.run();
            passwordField.pseudoClassStateChanged(VALID, registerModel.isIsPasswordValid());
            passwordField.pseudoClassStateChanged(INVALID, !registerModel.isIsPasswordValid());
        });

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("text-field-label");

        Label passwordError = new Label();
        passwordError.getStyleClass().add("text-field-error");
        addErrorAnimation(passwordError, registerModel.passwordErrorMessageProperty());

        VBox passwordBoxGroup = new VBox(passwordLabel, passwordField, passwordError);
        VBox.setMargin(passwordBoxGroup, new Insets(0, 0, 15,0));
        return passwordBoxGroup;
    }

    /** Creates role selection combo box populated with available user roles */
    private Node createRoleSelectionBox() {
        roleComboBox = new ComboBox<>();
        roleComboBox.getStyleClass().add("form-combo");
        
        // Populate with available roles from enum
        for (ROLE role : ROLE.values()) {
            roleComboBox.getItems().add(role.name());
        }
        
        roleComboBox.setPromptText("Select Role");
        registerModel.roleProperty().bindBidirectional(roleComboBox.valueProperty());
        return roleComboBox;
    }

    /** 
     * Creates registration button and login link with action handlers.
     * Manages button states during operations and provides auto-navigation after success.
     */
    private Node createButtonBox() {
        Hyperlink loginLink = new Hyperlink("Already have an account? Login Here!");
        loginLink.getStyleClass().add("form-link");

        Button register = new Button("Register");
        register.getStyleClass().add("form-button");

        // Login link action with button state management
        loginLink.setOnAction(event -> {
            loginLink.setDisable(true);
            register.setDisable(true);
            switchToLoginView.accept(()->{
                loginLink.setDisable(false);
                register.setDisable(false);
            });
        });

        // Register button action with state management and auto-navigation
        register.setOnAction(event -> {
            loginLink.setDisable(true);
            register.setDisable(true);
            handleRegister.accept(()->{
                // Auto-hide loading and navigate to login after 3 seconds
                PauseTransition hideLogin = new PauseTransition(Duration.seconds(3));
                hideLogin.playFromStart();

                hideLogin.setOnFinished(event1 ->{
                    onRegisterComplete.run();
                    switchToLoginView.accept(()->{
                        loginLink.setDisable(false);
                        register.setDisable(false);
                    });
                });
            });
        });

        VBox buttonBoxGroup = new VBox(register, loginLink);
        buttonBoxGroup.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonBoxGroup, new Insets(20,0,0,0));
        buttonBoxGroup.setSpacing(15);
        return buttonBoxGroup;
    }
}
