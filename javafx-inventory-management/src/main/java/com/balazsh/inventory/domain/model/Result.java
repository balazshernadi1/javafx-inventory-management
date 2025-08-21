package com.balazsh.inventory.domain.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Result {

    private final StringProperty status;
    private final StringProperty message;

    public Result(String status, String message) {
        this.status = new SimpleStringProperty(status);
        this.message = new SimpleStringProperty(message);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }
}
