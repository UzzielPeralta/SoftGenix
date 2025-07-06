package com.softgenix.Controller;

import com.softgenix.Application.App;
import com.softgenix.Application.Database;
import com.softgenix.Utils.Path;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CRegistro {
    @FXML
    void InicioSesion(ActionEvent event) {
        App.app.setScene(Path.Sesion);
    }

    @FXML
    private TextField emailField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox termsCheckBox;

    // 1. Añade el campo para el CheckBox de administrador
    //    Asegúrate de que en tu archivo Registro.fxml el CheckBox tenga fx:id="adminCheckBox"
    @FXML
    private CheckBox adminCheckBox;

    @FXML
    void registrarUsuario(ActionEvent event) {
        String name = nameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "Todos los campos son obligatorios.");
            return;
        }

        // --- CAMBIOS AQUÍ ---
        // Combinar nombre y apellido
        String nombreUsuario = name + " " + lastName;

        // 2. Determinar el rol basado en si el CheckBox está marcado
        String rol = adminCheckBox.isSelected() ? "Administrador" : "Encargado";

        // 3. Llamar al método de la base de datos con el rol determinado
        boolean success = Database.addUser(nombreUsuario, email, password, rol);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", "Usuario creado correctamente. Ahora puede iniciar sesión.");
            App.app.setScene(Path.Sesion);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error en la Base de Datos", "No se pudo registrar el usuario. Es posible que el correo ya exista.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
