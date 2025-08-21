package com.balazsh.inventory.mainmvci;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Builder;

public class View implements Builder<Region> {

    private final Model model;
    private final Region authView;
    private final Region dashboardView;

    public View(Model mainModel, Region authView, Region dashboardView) {
        this.model = mainModel;
        this.authView = authView;
        this.dashboardView = dashboardView;
    }

    @Override
    public Region build() {
        authView.visibleProperty().bind(model.authPageSelectedProperty());
        authView.managedProperty().bind(model.authPageSelectedProperty());

        dashboardView.visibleProperty().bind(model.dashboardPageSelectedProperty());
        dashboardView.managedProperty().bind(model.dashboardPageSelectedProperty());

        return new StackPane(authView, dashboardView);
    }
}
