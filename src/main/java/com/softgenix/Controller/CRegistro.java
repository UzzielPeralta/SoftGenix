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


}
