package com.softgenix.Controller;

import com.softgenix.Application.App;
import com.softgenix.Utils.Path;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CSesion {

    @FXML
    private VBox formPanel;

    @FXML
    private HBox mainContainer;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private TextField txtCorreo;

    @FXML
    private VBox welcomePanel;

    @FXML
    void CrearCuenta(ActionEvent event) {
        App.app.setScene(Path.Registrar);
    }

    // Método de ayuda para mostrar alertas (puedes copiarlo de CRegistro.java)
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void crear(ActionEvent event) {
        try {
            App.app.setScene(Path.Registrar);
        } catch (Exception e) {
            System.err.println("Error al cambiar de escena: " + e.getMessage());
        }

    }
}






