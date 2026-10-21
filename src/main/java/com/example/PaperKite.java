package com.example;

import com.example.ui.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

/** Entry point. All screens live in com.example.ui; data classes in model/service. */
public class PaperKite extends Application {

    public static final String APP_VERSION = "1.1.0";

    @Override
    public void start(Stage stage) {
        Navigator navigator = new Navigator(stage);
        navigator.showWelcome();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
