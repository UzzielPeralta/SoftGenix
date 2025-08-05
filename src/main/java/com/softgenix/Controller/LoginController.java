package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Utils.Path;
import com.softgenix.Model.User;
import com.softgenix.Service.AuthService;
import com.softgenix.App.Utils.Auth;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    @FXML
    void InicioSesion(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validación rápida
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Por favor, complete todos los campos");
            return;
        }

        // Deshabilitar botón para evitar múltiples clicks
        loginButton.setDisable(true);

        // Login asíncrono para mejor UX
        Task<User> loginTask = new Task<User>() {
            @Override
            protected User call() throws Exception {
                return AuthService.iniciarSesion(email, password);
            }

            @Override
            protected void succeeded() {
                loginButton.setDisable(false);
                User usuario = getValue();

                if (usuario != null) {
                    Auth.setUsuarioActual(usuario);
                    navegarSegunRol(usuario.getRol());
                } else {
                    showAlert("Credenciales incorrectas");
                }
            }

            @Override
            protected void failed() {
                loginButton.setDisable(false);
                showAlert("Error de conexión. Inténtelo de nuevo.");
            }
        };

        new Thread(loginTask).start();
    }

    private void navegarSegunRol(String rol) {
        switch (rol) {
            case "SUPERADMIN":
                App.app.setMainScene(Path.Dashboard);
                break;
            case "ADMIN":
                App.app.setMainScene(Path.DashboardAdmin);
                break;
            case "USER":
                App.app.setMainScene(Path.TaskUser);
                break;
            default:
                showAlert("Rol de usuario no reconocido");
                break;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}