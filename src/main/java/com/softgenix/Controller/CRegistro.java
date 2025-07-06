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



        String nombreUsuario = name + " " + lastName;
        String rol = adminCheckBox.isSelected() ? "Administrador" : "Usuario";

        // Obtener el botón que se hizo clic
        javafx.scene.control.Button btn = (javafx.scene.control.Button)event.getSource();
        btn.setDisable(true);
        btn.setText("Conectando...");

        javafx.concurrent.Task<Boolean> registroTask = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    return Database.addUser(nombreUsuario, email, password, rol);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        };

        registroTask.setOnSucceeded(e -> {
            btn.setDisable(false);
            btn.setText("Crear cuenta");

            boolean success = registroTask.getValue();
            if (success) {
                App.app.setScene(Path.Sesion);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error en la Base de Datos",
                        "No se pudo registrar el usuario. Es posible que el correo ya exista.");
            }
        });

        registroTask.setOnFailed(e -> {
            btn.setDisable(false);
            btn.setText("Crear cuenta");

            Throwable ex = registroTask.getException();
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Conexión",
                    "No se pudo conectar a la base de datos: " + ex.getMessage());
        });

        new Thread(registroTask).start();
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
