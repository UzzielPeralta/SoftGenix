package com.softgenix.Controller;

import com.softgenix.Application.App;
import com.softgenix.Dao.Database;
import com.softgenix.Utils.Path;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CSesion {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    void InicioSesion(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // 1. Validaciones básicas
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "El correo y la contraseña son obligatorios.");
            return;
        }

        // 2. Llamar al método de validación de la base de datos
        boolean isValid = Database.validateUser(email, password);

        // 3. Mostrar un mensaje según el resultado
        if (isValid) {
            showAlert(Alert.AlertType.INFORMATION, "Inicio de Sesión Exitoso", "¡Bienvenido!");
            App.app.setScene(Path.Main);

        } else {
            showAlert(Alert.AlertType.ERROR, "Error de Inicio de Sesión", "El correo o la contraseña son incorrectos.");
        }

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





