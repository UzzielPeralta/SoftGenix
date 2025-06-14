package com.softgenix.Application;

import com.softgenix.Controller.CAlerta;
import com.softgenix.Utils.Path;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
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
            AnchorPane pane = fxmlLoader.load();
            Scene scene = new Scene(pane);

            // Aplica solo el estilo general (login u otra pantalla principal)
            URL cssUrl = getClass().getResource(Path.LoginCss);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("No se encontró el archivo CSS en: " + Path.LoginCss);
            }

            stageWindow.setScene(scene);
            stageWindow.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void showCustomAlert(String message) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/Alerta.fxml"));
            Parent root = loader.load();

            CAlerta controller = loader.getController();
            Stage alertStage = new Stage();
            controller.setDialogStage(alertStage);
            controller.setMessage(message);

            Scene scene = new Scene(root);

            // Aplica el CSS SOLO para la alerta
            URL cssUrl = App.class.getResource("/Styles/Alerta.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            alertStage.setScene(scene);
            alertStage.setResizable(false);
            alertStage.initModality(Modality.APPLICATION_MODAL);
            alertStage.setTitle("Advertencia");
            alertStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
