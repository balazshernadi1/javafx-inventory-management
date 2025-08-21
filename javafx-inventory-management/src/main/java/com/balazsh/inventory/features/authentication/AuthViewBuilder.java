package com.balazsh.inventory.features.authentication;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Builder;

/**
 * Authentication view builder managing login/register form switching.
 * Uses StackPane overlay with visibility binding for seamless transitions.
 */
public class AuthViewBuilder implements Builder<Region> {

    private final Region loginView;
    private final Region registerView;
    private final AuthModel authModel;

    public AuthViewBuilder(Region loginView, Region registerView, AuthModel authModel) {
        this.loginView = loginView;
        this.registerView = registerView;
        this.authModel = authModel;
    }

    /** 
     * Builds authentication view with reactive visibility binding.
     * Only one form is visible at a time based on model state.
     */
    @Override
    public Region build() {
       // Bind login form visibility to model state
       loginView.visibleProperty().bind(authModel.showLoginProperty());
       loginView.managedProperty().bind(authModel.showLoginProperty());

       // Bind registration form visibility to model state
       registerView.visibleProperty().bind(authModel.showRegisterProperty());
       registerView.managedProperty().bind(authModel.showRegisterProperty());

       return new StackPane(loginView, registerView);
    }
}
