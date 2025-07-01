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
    // Este método ya lo tenías, para ir a la pantalla de sesión
    @FXML
    void InicioSesion(ActionEvent event) {
        App.app.setScene(Path.Sesion);
    }
    //Campos FXML existentes
    @FXML
    private TextField emailField; //

    @FXML
    private TextField lastNameField; //

    @FXML
    private TextField nameField; //

    @FXML
    private PasswordField passwordField; //

    @FXML
    private CheckBox termsCheckBox; //

    @FXML
    void registrarUsuario(ActionEvent event) {
        // Obtener los datos de los campos de texto
        String name = nameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validaciones básicas
        if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "Todos los campos son obligatorios.");
            return;
        }


        //Llamar al metodo en DatabaseManager para añadir el usuario
        boolean success = Database.addUser(name, lastName, email, password);

        // Mostrar un mensaje al usuario
        if (success) {
            App.app.setScene(Path.Sesion);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error en la Base de Datos", "No se pudo registrar el usuario. Es posible que el correo ya exista.");
        }
    }

}
