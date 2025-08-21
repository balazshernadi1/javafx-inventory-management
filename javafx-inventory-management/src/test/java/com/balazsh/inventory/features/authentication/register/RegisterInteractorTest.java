package com.balazsh.inventory.features.authentication.register;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class RegisterInteractorTest {

    private RegisterModel registerModel;
    private RegisterInteractor registerInteractor;

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
        registerModel = new RegisterModel();
        registerInteractor = new RegisterInteractor(registerModel);
    }

    @Test
    void isPasswordValid_ShouldSetValidToTrue_WhenPasswordMeetsAllRequirements() {
        // Given
        registerModel.passwordProperty().set("ValidPass123!");

        // When
        registerInteractor.isPasswordValid();

        // Then
        assertTrue(registerModel.isIsPasswordValid());
        assertNull(registerModel.getPasswordErrorMessage());
    }

    @Test
    void isPasswordValid_ShouldSetValidToFalse_WhenPasswordIsTooShort() {
        // Given
        registerModel.passwordProperty().set("Abc1!");

        // When
        registerInteractor.isPasswordValid();

        // Then
        assertFalse(registerModel.isIsPasswordValid());
        assertTrue(registerModel.getPasswordErrorMessage().contains("at least 6 characters long"));
    }

    @Test
    void isPasswordValid_ShouldSetValidToFalse_WhenPasswordMissingUppercase() {
        // Given
        registerModel.passwordProperty().set("validpass123!");

        // When
        registerInteractor.isPasswordValid();

        // Then
        assertFalse(registerModel.isIsPasswordValid());
        assertTrue(registerModel.getPasswordErrorMessage().contains("uppercase letter"));
    }

    @Test
    void isPasswordValid_ShouldSetValidToFalse_WhenPasswordMissingLowercase() {
        // Given
        registerModel.passwordProperty().set("VALIDPASS123!");

        // When
        registerInteractor.isPasswordValid();

        // Then
        assertFalse(registerModel.isIsPasswordValid());
        assertTrue(registerModel.getPasswordErrorMessage().contains("lowercase letter"));
    }

    @Test
    void isPasswordValid_ShouldSetValidToFalse_WhenPasswordMissingDigit() {
        // Given
        registerModel.passwordProperty().set("ValidPass!");

        // When
        registerInteractor.isPasswordValid();

        // Then
        assertFalse(registerModel.isIsPasswordValid());
        assertTrue(registerModel.getPasswordErrorMessage().contains("digit"));
    }

    @Test
    void isPasswordValid_ShouldSetValidToFalse_WhenPasswordMissingSpecialCharacter() {
        // Given
        registerModel.passwordProperty().set("ValidPass123");

        // When
        registerInteractor.isPasswordValid();

        // Then
        assertFalse(registerModel.isIsPasswordValid());
        assertTrue(registerModel.getPasswordErrorMessage().contains("special character"));
    }

    @Test
    void isPasswordValid_ShouldSetValidToFalse_WhenPasswordMissingMultipleRequirements() {
        // Given
        registerModel.passwordProperty().set("abc");

        // When
        registerInteractor.isPasswordValid();

        // Then
        assertFalse(registerModel.isIsPasswordValid());
        String errorMessage = registerModel.getPasswordErrorMessage();
        assertTrue(errorMessage.contains("at least 6 characters long"));
        assertTrue(errorMessage.contains("uppercase letter"));
        assertTrue(errorMessage.contains("digit"));
        assertTrue(errorMessage.contains("special character"));
    }

    @Test
    void isUsernameValid_ShouldSetValidToTrue_WhenUsernameIsAtLeastSixCharacters() {
        // Given
        registerModel.usernameProperty().set("validuser");

        // When
        registerInteractor.isUsernameValid();

        // Then
        assertTrue(registerModel.isIsUsernameValid());
    }

    @Test
    void isUsernameValid_ShouldSetValidToTrue_WhenUsernameIsExactlySixCharacters() {
        // Given
        registerModel.usernameProperty().set("user12");

        // When
        registerInteractor.isUsernameValid();

        // Then
        assertTrue(registerModel.isIsUsernameValid());
    }

    @Test
    void isUsernameValid_ShouldSetValidToFalse_WhenUsernameIsTooShort() {
        // Given
        registerModel.usernameProperty().set("user");

        // When
        registerInteractor.isUsernameValid();

        // Then
        assertFalse(registerModel.isIsUsernameValid());
        assertTrue(registerModel.getUsernameErrorMessage().contains("at least 6 characters long"));
    }

    @Test
    void isUsernameValid_ShouldSetValidToFalse_WhenUsernameIsEmpty() {
        // Given
        registerModel.usernameProperty().set("");

        // When
        registerInteractor.isUsernameValid();

        // Then
        assertFalse(registerModel.isIsUsernameValid());
        assertTrue(registerModel.getUsernameErrorMessage().contains("at least 6 characters long"));
    }

    @Test
    void preRegisterValidation_ShouldReturnTrue_WhenAllFieldsAreValid() {
        // Given
        registerModel.isPasswordValidProperty().set(true);
        registerModel.isUsernameValidProperty().set(true);
        registerModel.roleProperty().set("admin");

        // When
        boolean result = registerInteractor.preRegisterValidation();

        // Then
        assertTrue(result);
    }

    @Test
    void preRegisterValidation_ShouldReturnFalse_WhenPasswordIsInvalid() throws InterruptedException {
        // Given
        registerModel.isPasswordValidProperty().set(false);
        registerModel.isUsernameValidProperty().set(true);
        registerModel.roleProperty().set("admin");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean result = new AtomicBoolean();

        // When - Run on JavaFX Application Thread to handle Alert creation
        Platform.runLater(() -> {
            try {
                result.set(registerInteractor.preRegisterValidation());
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        // Then
        assertFalse(result.get());
    }

    @Test
    void preRegisterValidation_ShouldReturnFalse_WhenUsernameIsInvalid() throws InterruptedException {
        // Given
        registerModel.isPasswordValidProperty().set(true);
        registerModel.isUsernameValidProperty().set(false);
        registerModel.roleProperty().set("admin");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean result = new AtomicBoolean();

        // When - Run on JavaFX Application Thread to handle Alert creation
        Platform.runLater(() -> {
            try {
                result.set(registerInteractor.preRegisterValidation());
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        // Then
        assertFalse(result.get());
    }

    @Test
    void preRegisterValidation_ShouldReturnFalse_WhenRoleIsEmpty() throws InterruptedException {
        // Given
        registerModel.isPasswordValidProperty().set(true);
        registerModel.isUsernameValidProperty().set(true);
        registerModel.roleProperty().set("");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean result = new AtomicBoolean();

        // When - Run on JavaFX Application Thread to handle Alert creation
        Platform.runLater(() -> {
            try {
                result.set(registerInteractor.preRegisterValidation());
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        // Then
        assertFalse(result.get());
    }

    @Test
    void preRegisterValidation_ShouldReturnFalse_WhenAllFieldsAreInvalid() throws InterruptedException {
        // Given
        registerModel.isPasswordValidProperty().set(false);
        registerModel.isUsernameValidProperty().set(false);
        registerModel.roleProperty().set("");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean result = new AtomicBoolean();

        // When - Run on JavaFX Application Thread to handle Alert creation
        Platform.runLater(() -> {
            try {
                result.set(registerInteractor.preRegisterValidation());
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        // Then
        assertFalse(result.get());
    }
}