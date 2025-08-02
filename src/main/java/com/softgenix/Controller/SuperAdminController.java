package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Utils.Auth;
import com.softgenix.App.Utils.Path;
import com.softgenix.Model.User;
import com.softgenix.Service.UserService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SuperAdminController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField nombreField;

    @FXML
    private ComboBox<String> rolComboBox;

    @FXML
    private TableView<User> usuariosTableView;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nombreColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> rolColumn;

    @FXML
    private VBox welcomePanel;

    @FXML
    private AnchorPane usuariosPanel;

    @FXML
    private AnchorPane tablerosPanel;

    @FXML
    private Label headerTitleLabel;

    @FXML
    private Button crearTableroBtn;

    @FXML
    private Button addListBtn;

    @FXML
    private Button crearAdminBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar ComboBox de roles
        rolComboBox.setItems(FXCollections.observableArrayList("ADMIN"));

        // Configurar columnas de la tabla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }

    @FXML
    private void mostrarGestionUsuarios(ActionEvent event) {
        // Ocultar otros paneles
        welcomePanel.setVisible(false);
        tablerosPanel.setVisible(false);

        // Mostrar panel de usuarios
        usuariosPanel.setVisible(true);

        // Cambiar título del header
        headerTitleLabel.setText("Gestión de Admin");

        // Cargar usuarios
        cargarUsuarios();
    }

    @FXML
    private void mostrarGestionTableros(ActionEvent event) {
        // Ocultar otros paneles
        welcomePanel.setVisible(false);
        usuariosPanel.setVisible(false);

        // Mostrar panel de tableros
        tablerosPanel.setVisible(true);

        // Cambiar título del header
        headerTitleLabel.setText("Mi tablero de Zendo");
    }

    @FXML
    private void crearUsuario(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String nombre = nombreField.getText();
        String rol = rolComboBox.getValue();

        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty() || rol == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios");
            return;
        }

        boolean creado = UserService.crearUsuario(email, password, nombre, rol);
        if (creado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Admin creado correctamente");
            limpiarFormulario();
            cargarUsuarios();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el admin");
        }
    }

    @FXML
    private void eliminarUsuario(ActionEvent event) {
        User usuarioSeleccionado = usuariosTableView.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un admin para eliminar");
            return;
        }

        boolean eliminado = UserService.eliminarUsuario(usuarioSeleccionado.getId());
        if (eliminado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Admin eliminado correctamente");
            cargarUsuarios();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el admin");
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Auth.cerrarSesion();
        App.app.setScene(Path.Login);
    }

    private void cargarUsuarios() {
        List<User> usuarios = UserService.obtenerTodosUsuarios();
        usuariosTableView.setItems(FXCollections.observableArrayList(usuarios));
    }

    private void limpiarFormulario() {
        emailField.clear();
        passwordField.clear();
        nombreField.clear();
        rolComboBox.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
