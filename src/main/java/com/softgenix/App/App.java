package com.softgenix.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class App extends Application {
    public static App app;
    private Stage stageWindow;


    private static final Map<String, Scene> sceneCache = new HashMap<>();


    private double mainWindowWidth;
    private double mainWindowHeight;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        app = this;
        stageWindow = stage;


        javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
        javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
        mainWindowWidth = bounds.getWidth() * 0.9;
        mainWindowHeight = bounds.getHeight() * 0.9;

        setLoginScene();
        stageWindow.setTitle("Zendo - Inicio de sesión");
        stageWindow.show();
    }


    public void setLoginScene() {
        Scene scene = loadScene("/FXML/Login.fxml");
        if (scene != null) {
            stageWindow.setScene(scene);
            stageWindow.setResizable(false);


            stageWindow.setWidth(825.0);
            stageWindow.setHeight(625.0);

            stageWindow.centerOnScreen();
        }
    }


    public void setMainScene(String fxmlPath) {
        Scene scene = loadScene(fxmlPath);
        if (scene != null) {
            stageWindow.setScene(scene);
            stageWindow.setResizable(true);


            stageWindow.setWidth(mainWindowWidth);
            stageWindow.setHeight(mainWindowHeight);
            stageWindow.centerOnScreen();

            stageWindow.setTitle("Zendo - Gestión de Tareas");
        }
    }


    public void setScene(String fxmlPath) {
        Scene scene = loadScene(fxmlPath);
        if (scene != null) {
            stageWindow.setScene(scene);
        }
    }


    private Scene loadScene(String fxmlPath) {
        try {

            if (sceneCache.containsKey(fxmlPath)) {
                return sceneCache.get(fxmlPath);
            }

            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                System.err.println("No se encontró el archivo FXML en: " + fxmlPath);
                return null;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Scene scene = new Scene(loader.load());
            if (shouldCache(fxmlPath)) {
                sceneCache.put(fxmlPath, scene);
            }

            return scene;

        } catch (Exception e) {
            System.err.println("Error cargando FXML: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }


    private boolean shouldCache(String fxmlPath) {
       
        return !fxmlPath.contains("User.fxml") && !fxmlPath.contains("Dashboard");
    }

    public static void clearCache() {
        sceneCache.clear();
    }
}

