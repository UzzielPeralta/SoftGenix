package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Utils.Path;
import com.softgenix.Model.User;
import com.softgenix.Service.AuthService;
import com.softgenix.App.Utils.Auth;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    void InicioSesion(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validaciones básicas
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "El correo y la contraseña son obligatorios.");
            return;
        }

        // Intentar iniciar sesión
        User usuario = AuthService.iniciarSesion(email, password);

        if (usuario != null) {
            // Almacenar usuario en sesión
            Auth.setUsuarioActual(usuario);

            // Redirigir según el rol
            switch (usuario.getRol()) {
                case "SUPERADMIN":
                    App.app.setScene(Path.Dashboard); // Pantalla de administración
                    break;
                case "ADMIN":
                    App.app.setScene(Path.DashboardAdmin); // Pantalla de tableros
                    break;
                case "USER":
                    App.app.setScene(Path.TaskUser); // Pantalla de tareas asignadas
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Error", "Rol no reconocido");
                    break;
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error de Inicio de Sesión", "El correo o la contraseña son incorrectos.");
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



