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

    // Cache para evitar recargar FXML repetidamente
    private static final Map<String, Scene> sceneCache = new HashMap<>();

    // Dimensiones precalculadas
    private double mainWindowWidth;
    private double mainWindowHeight;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        app = this;
        stageWindow = stage;

        // Precalcular dimensiones una sola vez
        javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
        javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
        mainWindowWidth = bounds.getWidth() * 0.9;
        mainWindowHeight = bounds.getHeight() * 0.9;

        setLoginScene();
        stageWindow.setTitle("Zendo - Inicio de sesión");
        stageWindow.show();
    }

    /**
     * Configurar escena de login con ventana fija (optimizada)
     */
    /**
     * Configurar escena de login con ventana fija (optimizada)
     */
    public void setLoginScene() {
        Scene scene = loadScene("/FXML/Login.fxml");
        if (scene != null) {
            stageWindow.setScene(scene);
            stageWindow.setResizable(false);

            // FORZAR el tamaño del login (800x600 según tu Login.fxml)
            stageWindow.setWidth(825.0);
            stageWindow.setHeight(625.0);

            stageWindow.centerOnScreen();
        }
    }

    /**
     * Configurar escena principal optimizada
     */
    public void setMainScene(String fxmlPath) {
        Scene scene = loadScene(fxmlPath);
        if (scene != null) {
            stageWindow.setScene(scene);
            stageWindow.setResizable(true);

            // Usar dimensiones precalculadas
            stageWindow.setWidth(mainWindowWidth);
            stageWindow.setHeight(mainWindowHeight);
            stageWindow.centerOnScreen();

            stageWindow.setTitle("Zendo - Gestión de Tareas");
        }
    }

    /**
     * Método genérico optimizado
     */
    public void setScene(String fxmlPath) {
        Scene scene = loadScene(fxmlPath);
        if (scene != null) {
            stageWindow.setScene(scene);
        }
    }

    /**
     * Método optimizado para cargar escenas con cache
     */
    private Scene loadScene(String fxmlPath) {
        try {
            // Verificar cache primero
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

            // Guardar en cache (solo para pantallas que no cambien frecuentemente)
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

    /**
     * Determinar qué pantallas cachear
     */
    private boolean shouldCache(String fxmlPath) {
        // No cachear pantallas que se actualizan frecuentemente
        return !fxmlPath.contains("User.fxml") && !fxmlPath.contains("Dashboard");
    }

    /**
     * Limpiar cache si es necesario
     */
    public static void clearCache() {
        sceneCache.clear();
    }
}

