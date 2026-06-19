package com.theater;

import com.theater.utils.DatabaseHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TheaterApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseHelper.initializeDatabase();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/theater/login.fxml"));
        Parent root = loader.load();

        stage.setTitle("Theater - 電影院訂票系統");
        stage.setScene(new Scene(root, 640, 480));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
