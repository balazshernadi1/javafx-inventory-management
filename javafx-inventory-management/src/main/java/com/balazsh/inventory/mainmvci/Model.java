package com.balazsh.inventory.mainmvci;

import com.balazsh.inventory.domain.model.ActiveUserDetails;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Model {

    private final BooleanProperty authPageSelected = new SimpleBooleanProperty(true);
    private final ObjectProperty<ActiveUserDetails> activeUserDetails = new SimpleObjectProperty<>(new ActiveUserDetails("", null, null));
    private final BooleanProperty dashboardPageSelected = new SimpleBooleanProperty(false);


    public boolean isDashboardPageSelected() {
        return dashboardPageSelected.get();
    }

    public BooleanProperty dashboardPageSelectedProperty() {
        return dashboardPageSelected;
    }

    public boolean isAuthPageSelected() {
        return authPageSelected.get();
    }

    public BooleanProperty authPageSelectedProperty() {
        return authPageSelected;
    }

    public ActiveUserDetails getActiveUserDetails() {
        return activeUserDetails.get();
    }

    public ObjectProperty<ActiveUserDetails> activeUserDetailsProperty() {
        return activeUserDetails;
    }
}
