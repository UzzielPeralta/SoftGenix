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


    @FXML
    void IniciarSesion(MouseEvent event) {
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        App.app.setScene(Path.Main);
    }
}




