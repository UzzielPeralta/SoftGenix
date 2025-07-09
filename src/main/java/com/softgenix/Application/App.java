package com.softgenix.Application;

import com.softgenix.Utils.Path;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
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
        setScene(Path.Sesion);
    }

    public void setScene(String fxmlPath) {
        URL fxmlLocation = getClass().getResource(fxmlPath);

        if (fxmlLocation == null) {
            System.err.println("No se encontró el archivo FXML en: " + fxmlPath);
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            Parent pane = fxmlLoader.load();
            Scene scene = new Scene(pane);
            stageWindow.setScene(scene);

            // 👉 Aquí controlas según la pantalla:
            if (fxmlPath.equals(Path.Sesion) || fxmlPath.equals(Path.Registrar)) {
                stageWindow.setResizable(false);  // Solo login y registro NO se agrandan
            } else {
                stageWindow.setResizable(true);   // Las demás sí se pueden agrandar
            }
            if (fxmlPath.equals(Path.Main)) {
                stageWindow.setFullScreen(true);
            }

            stageWindow.show();
            // Agregado punto y coma aquí

            // Determinar qué CSS cargar según la vista
            String cssPath;
            if (fxmlPath.contains("Sesion")) {
                cssPath = Path.LoginCss;
            } else if (fxmlPath.contains("Registro")) {
                cssPath = Path.RegistroCss; // Usa constante en lugar de valor literal
            } else if (fxmlPath.contains("Tablero")) {
                cssPath = Path.TableroCss; // Usa constante en lugar de valor literal
            } else {
                cssPath = Path.LoginCss; // CSS por defecto
            }

            // Declaración de cssUrl añadida aquí
            URL cssUrl = getClass().getResource(cssPath);

            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("No se encontró el archivo CSS en: " + cssPath);
            }

            stageWindow.setScene(scene);
            stageWindow.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }
}

