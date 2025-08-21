package com.balazsh.inventory.mainmvci;

import com.balazsh.inventory.features.authentication.AuthController;
import com.balazsh.inventory.features.dashboard.DashboardController;
import javafx.scene.layout.Region;


/**
 * The parent controller which handles data flow between other MVCI controllers.
 * This is the only controller which should be declared and instantiated within a main JavaFX application class.
 */
public class Controller {

    private final View mainView;
    private final Interactor interactor;
    private final Model mainModel;
    private final AuthController authController;
    private final DashboardController dashboardController;

    public Controller() {
        mainModel = new Model();
        authController = new AuthController(mainModel.activeUserDetailsProperty(), this::switchToDashboard);
        dashboardController = new DashboardController(this::switchToAuthPage, mainModel.activeUserDetailsProperty());
        interactor = new Interactor(mainModel);
        mainView = new View(mainModel, authController.getView(), dashboardController.getView());
    }

    private void switchToDashboard(){
        mainModel.authPageSelectedProperty().set(false);
        mainModel.dashboardPageSelectedProperty().set(true);
    }

    private void switchToAuthPage(){
        /* Resets the active user details so a login action may populate it again */
        mainModel.activeUserDetailsProperty().set(null);

        mainModel.authPageSelectedProperty().set(true);
        mainModel.dashboardPageSelectedProperty().set(false);
    }

    public Region getView(){
        return mainView.build();
    }

}
