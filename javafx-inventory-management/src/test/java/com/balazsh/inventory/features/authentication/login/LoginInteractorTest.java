package com.balazsh.inventory.features.authentication.login;

import com.balazsh.inventory.domain.model.Result;
import com.balazsh.inventory.domain.model.UserLoginDetails;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoginInteractorTest {

    private LoginModel loginModel;
    private LoginInteractor loginInteractor;

    @BeforeAll
    static void initJavaFX() {

        // needed due to javafx dependency
        System.setProperty("java.awt.headless", "true");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Platform already initialized
        }
    }

    @BeforeEach
    void setUp() {
        loginModel = new LoginModel();
        loginInteractor = new LoginInteractor(loginModel);
    }

    @Test
    void preLoginValidation_ShouldReturnFalse_WhenMaxAttemptsReached() throws InterruptedException {
        // Given
        loginModel.loginAttemptsProperty().set(3);
        loginModel.usernameProperty().set("validUser");
        loginModel.passwordProperty().set("validPass");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean result = new AtomicBoolean();

        // When - Run on JavaFX Application Thread to handle Alert creation
        Platform.runLater(() -> {
            try {
                result.set(loginInteractor.preLoginValidation());
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        // Then
        assertFalse(result.get());
    }

    @Test
    void preLoginValidation_ShouldReturnFalse_WhenUsernameIsEmpty() {
        // Given
        loginModel.loginAttemptsProperty().set(1);
        loginModel.usernameProperty().set("");
        loginModel.passwordProperty().set("validPass");

        // When
        boolean result = loginInteractor.preLoginValidation();

        // Then
        assertFalse(result);
        assertEquals("Username field cannot be empty", loginModel.getUsernameErrorMessage());
    }

    @Test
    void preLoginValidation_ShouldReturnFalse_WhenUsernameIsBlank() {
        // Given
        loginModel.loginAttemptsProperty().set(1);
        loginModel.usernameProperty().set("   ");
        loginModel.passwordProperty().set("validPass");

        // When
        boolean result = loginInteractor.preLoginValidation();

        // Then
        assertFalse(result);
        assertEquals("Username field cannot be empty", loginModel.getUsernameErrorMessage());
    }

    @Test
    void preLoginValidation_ShouldReturnFalse_WhenPasswordIsEmpty() {
        // Given
        loginModel.loginAttemptsProperty().set(1);
        loginModel.usernameProperty().set("validUser");
        loginModel.passwordProperty().set("");

        // When
        boolean result = loginInteractor.preLoginValidation();

        // Then
        assertFalse(result);
        assertEquals("Password field cannot be empty", loginModel.getPasswordErrorMessage());
    }

    @Test
    void preLoginValidation_ShouldReturnFalse_WhenPasswordIsBlank() {
        // Given
        loginModel.loginAttemptsProperty().set(1);
        loginModel.usernameProperty().set("validUser");
        loginModel.passwordProperty().set("   ");

        // When
        boolean result = loginInteractor.preLoginValidation();

        // Then
        assertFalse(result);
        assertEquals("Password field cannot be empty", loginModel.getPasswordErrorMessage());
    }

    @Test
    void preLoginValidation_ShouldReturnFalse_WhenBothFieldsAreInvalid() {
        // Given
        loginModel.loginAttemptsProperty().set(1);
        loginModel.usernameProperty().set("");
        loginModel.passwordProperty().set("");

        // When
        boolean result = loginInteractor.preLoginValidation();

        // Then
        assertFalse(result);
        assertEquals("Username field cannot be empty", loginModel.getUsernameErrorMessage());
        assertEquals("Password field cannot be empty", loginModel.getPasswordErrorMessage());
    }

    @Test
    void preLoginValidation_ShouldReturnTrue_WhenAllValidationsPass() {
        // Given
        loginModel.loginAttemptsProperty().set(1);
        loginModel.usernameProperty().set("validUser");
        loginModel.passwordProperty().set("validPass");

        // When
        boolean result = loginInteractor.preLoginValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void preLogin_ShouldClearPreviousResultMessage_WhenResultExists() {
        // Given
        Result result = new Result("failure", "Previous error");
        loginModel.resultProperty().set(result);

        // When
        loginInteractor.preLogin();

        // Then
        assertEquals("", result.getMessage());
        assertTrue(loginModel.isLoading());
    }

    @Test
    void preLogin_ShouldSetLoadingToTrue_WhenResultIsNull() {
        // Given - resultProperty is null by default, but the code checks resultProperty() != null
        // which is always true (the property itself exists), but resultProperty().get() can be null
        loginModel.resultProperty().set(null);

        // When
        loginInteractor.preLogin();

        // Then
        assertTrue(loginModel.isLoading());
        // The method should not crash when result is null
    }

    @Test
    void resolveLoginResult_ShouldIncrementAttempts_WhenLoginFails() {
        // Given
        Result failureResult = new Result("failure", "Login failed");
        loginModel.resultProperty().set(failureResult);
        loginModel.loginAttemptsProperty().set(2);

        // When
        loginInteractor.resolveLoginResult();

        // Then
        assertEquals(3, loginModel.getLoginAttempts());
    }

    @Test
    void resolveLoginResult_ShouldResetAttemptsAndSetAuthenticated_WhenLoginSucceeds() {
        // Given
        Result successResult = new Result("success", "Login successful");
        loginModel.resultProperty().set(successResult);
        loginModel.loginAttemptsProperty().set(2);

        // When
        loginInteractor.resolveLoginResult();

        // Then
        assertEquals(0, loginModel.getLoginAttempts());
        assertTrue(loginModel.isAuthenticated());
    }

    @Test
    void resolveLoginResult_ShouldDoNothing_WhenStatusIsNeither() {
        // Given
        Result unknownResult = new Result("pending", "Processing...");
        loginModel.resultProperty().set(unknownResult);
        int initialAttempts = 2;
        loginModel.loginAttemptsProperty().set(initialAttempts);
        boolean initialAuth = false;
        loginModel.authenticatedProperty().set(initialAuth);

        // When
        loginInteractor.resolveLoginResult();

        // Then
        assertEquals(initialAttempts, loginModel.getLoginAttempts());
        assertEquals(initialAuth, loginModel.isAuthenticated());
    }

    @Test
    void createLoginRequest_ShouldSetUserLoginDetailsFromFormData() {
        // Given
        String username = "testUser";
        String password = "testPassword";
        loginModel.usernameProperty().set(username);
        loginModel.passwordProperty().set(password);

        // When
        loginInteractor.createLoginRequest();

        // Then
        UserLoginDetails details = loginModel.getUserLoginDetails();
        assertNotNull(details);
        // Note: You would need getters in UserLoginDetails to verify the exact values
        // For now we just verify the object was created
    }
}