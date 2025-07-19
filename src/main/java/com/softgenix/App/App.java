package com.softgenix.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
public class App extends Application {
    public static App app;
    private Stage stageWindow;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        app = this;
        stageWindow = stage;
        setScene("/FXML/Login.fxml"); // Usa la ruta FXML que tengas definida
        stageWindow.setTitle("Zendo - Inicio de sesión");
        stageWindow.show();
    }

    public void setScene(String fxmlPath) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                System.err.println("No se encontró el archivo FXML en: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Scene scene = new Scene(loader.load());
            stageWindow.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

