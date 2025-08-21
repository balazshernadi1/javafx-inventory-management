package com.balazsh.inventory.domain.model;

import javafx.beans.property.*;

public class UserEntry {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty roleSelected = new SimpleStringProperty();
    private final StringProperty accountStatus = new SimpleStringProperty();
    private final BooleanProperty userSelected = new SimpleBooleanProperty();

    public UserEntry() {
        // Default constructor
    }

    public UserEntry(int id, String username, String role, String accountStatus) {
        this.id.set(id);
        this.username.set(username);
        this.roleSelected.set(role);
        this.accountStatus.set(accountStatus);
        this.userSelected.set(false);
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setRoleSelected(String roleSelected) {
        this.roleSelected.set(roleSelected);
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus.set(accountStatus);
    }

    public void setUserSelected(boolean userSelected) {
        this.userSelected.set(userSelected);
    }

    public boolean isUserSelected() {
        return userSelected.get();
    }

    public BooleanProperty userSelectedProperty() {
        return userSelected;
    }

    public String getAccountStatus() {
        return accountStatus.get();
    }

    public StringProperty accountStatusProperty() {
        return accountStatus;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getRoleSelected() {
        return roleSelected.get();
    }

    public StringProperty roleSelectedProperty() {
        return roleSelected;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }
}
