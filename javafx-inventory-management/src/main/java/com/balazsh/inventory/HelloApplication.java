package com.balazsh.inventory;

import com.balazsh.inventory.features.dashboard.DashboardController;
import com.balazsh.inventory.mainmvci.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);

    @Override
    public void start(Stage stage){
        Scene scene = new Scene(new Controller().getView());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}