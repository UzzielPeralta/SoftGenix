package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Config.Database;
import com.softgenix.App.Utils.Auth;
import com.softgenix.App.Utils.Path;
import com.softgenix.Dao.BoardDAO;
import com.softgenix.Dao.CardDAO;
import com.softgenix.Dao.UserDAO;
import com.softgenix.Model.Board;
import com.softgenix.Model.Card;
import com.softgenix.Model.Column;
import com.softgenix.Model.User;
import com.softgenix.Service.BoardService;
import com.softgenix.Service.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;



public class AdminController implements Initializable {

    @FXML private TableView<User> usuariosTableView;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nombreColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> rolColumn;
    @FXML private VBox welcomePanel;
    @FXML private AnchorPane usuariosPanel;
    @FXML private AnchorPane tablerosPanel;
    @FXML private AnchorPane tarjetasPanel;
    @FXML private AnchorPane asignacionPanel;
    @FXML private Label headerTitleLabel;

    // Formulario usuarios
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nombreField;

    // Tableros
    @FXML private TableView<Board> tablerosTableView;
    @FXML private TableColumn<Board, Integer> tableroIdColumn;
    @FXML private TableColumn<Board, String> tableroNombreColumn;
    @FXML private TableColumn<Board, String> tableroDescripcionColumn;

    // Asignaciones
    @FXML private ComboBox<String> tablerosComboBox;
    @FXML private TableView<User> usuariosDisponiblesTable;
    @FXML private TableColumn<User, Integer> idUsuarioColumn;
    @FXML private TableColumn<User, String> nombreUsuarioColumn;
    @FXML private TableColumn<User, String> emailUsuarioColumn;
    @FXML private TableView<User> usuariosAsignadosTable;
    @FXML private TableColumn<User, Integer> idAsignadoColumn;
    @FXML private TableColumn<User, String> nombreAsignadoColumn;
    @FXML private TableColumn<User, String> emailAsignadoColumn;

    // Tarjetas
    @FXML private Label tableroActualLabel;
    @FXML private TextField tarjetaTituloField;
    @FXML private TextArea tarjetaDescripcionField;
    @FXML private TableView<Card> tarjetasTableView;
    @FXML private TableColumn<Card, String> tarjetaTituloColumn;
    @FXML private TableColumn<Card, String> tarjetaDescripcionColumn;

    private Board tableroActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTodasLasColumnas();
        cargarUsuarios();
    }

    private void configurarTodasLasColumnas() {
        // Usuarios
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));

        // Tableros
        tableroIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableroNombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tableroDescripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Asignaciones
        idUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        idAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Tarjetas
        tarjetaTituloColumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tarjetaDescripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    private void ocultarTodosPaneles() {
        AnchorPane[] paneles = {usuariosPanel, tablerosPanel, tarjetasPanel, asignacionPanel};

        welcomePanel.setVisible(false);
        welcomePanel.setManaged(false);

        for (AnchorPane panel : paneles) {
            if (panel != null) {
                panel.setVisible(false);
                panel.setManaged(false);
            }
        }
    }

    // === GESTIÓN DE USUARIOS ===

    public void mostrarGestionUsuarios() {
        cambiarPanel(usuariosPanel, "Gestión de Usuario");
        cargarUsuarios();
    }

    @FXML
    private void crearUsuario(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String nombre = nombreField.getText().trim();

        if (!validarCamposUsuario(email, password, nombre)) return;

        ejecutarTareaAsincrona(
                () -> UserService.crearUsuario(email, password, nombre, "USER"),
                resultado -> {
                    if (resultado) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario creado correctamente");
                        limpiarFormulario();
                        cargarUsuarios();
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el usuario");
                    }
                }
        );
    }

    @FXML
    private void eliminarUsuario() {
        User usuarioSeleccionado = usuariosTableView.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un usuario para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este usuario?");
        confirmacion.setContentText("Admin: " + usuarioSeleccionado.getNombre() +
                "\nEsta acción no se puede deshacer.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            ejecutarTareaAsincrona(
                    () -> UserDAO.eliminarUsuario(usuarioSeleccionado.getId()),
                    eliminado -> {
                        if (eliminado) {
                            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                                    "Usuario eliminado correctamente");
                            cargarUsuarios();
                        } else {
                            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                                    "No se pudo eliminar el usuario");
                        }
                    }
            );
        }
    }

    private void cargarUsuarios() {
        ejecutarTareaAsincrona(
                () -> UserService.obtenerUsuariosPorRol("USER"),
                usuarios -> usuariosTableView.setItems(FXCollections.observableArrayList(usuarios))
        );
    }

    // === GESTIÓN DE TABLEROS ===

    @FXML
    private void mostrarGestionTableros(ActionEvent event) {
        cambiarPanel(tablerosPanel, "Gestión de Tableros");
        cargarTableros();
    }

    private void cargarTableros() {
        ejecutarTareaAsincrona(
                () -> BoardService.obtenerTablerosUsuario(),
                tableros -> tablerosTableView.setItems(FXCollections.observableArrayList(tableros))
        );
    }

    // === GESTIÓN DE TARJETAS ===

    @FXML
    private void gestionarTarjetasTablero() {
        tableroActual = tablerosTableView.getSelectionModel().getSelectedItem();
        if (tableroActual == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un tablero para gestionar sus tarjetas");
            return;
        }

        tablerosPanel.setVisible(false);
        tablerosPanel.setManaged(false);
        tableroActualLabel.setText("Mi tablero de " + tableroActual.getNombre());
        cargarTarjetasTablero();
        tarjetasPanel.setVisible(true);
        tarjetasPanel.setManaged(true);
    }

    @FXML
    private void volverATableros() {
        cambiarPanel(tablerosPanel, "Gestión de Tableros");
        cargarTableros();
    }

    @FXML
    private void crearTarjeta() {
        if (tableroActual == null) return;

        String titulo = tarjetaTituloField.getText().trim();
        String descripcion = tarjetaDescripcionField.getText().trim();

        if (titulo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El título de la tarjeta es obligatorio");
            return;
        }

        int columnaId = obtenerColumnaEnProceso(tableroActual.getId());
        if (columnaId == -1) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo encontrar una columna válida");
            return;
        }

        Card tarjeta = new Card();
        tarjeta.setTitulo(titulo);
        tarjeta.setDescripcion(descripcion);
        tarjeta.setColumnaId(columnaId);
        tarjeta.setCreadoPorUsuarioId(Auth.getUsuarioActual().getId());

        ejecutarTareaAsincrona(
                () -> CardDAO.crearTarjeta(tarjeta),
                resultado -> {
                    if (resultado) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tarjeta creada correctamente");
                        tarjetaTituloField.clear();
                        tarjetaDescripcionField.clear();
                        cargarTarjetasTablero();
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear la tarjeta");
                    }
                }
        );
    }

    @FXML
    private void eliminarTarjeta() {
        Card tarjeta = tarjetasTableView.getSelectionModel().getSelectedItem();
        if (tarjeta == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione una tarjeta para eliminar");
            return;
        }

        ejecutarTareaAsincrona(
                () -> CardDAO.eliminarTarjeta(tarjeta.getId()),
                resultado -> {
                    if (resultado) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tarjeta eliminada correctamente");
                        cargarTarjetasTablero();
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la tarjeta");
                    }
                }
        );
    }

    private void cargarTarjetasTablero() {
        if (tableroActual == null) return;

        ejecutarTareaAsincrona(
                () -> CardDAO.obtenerTarjetasPorTablero(tableroActual.getId()),
                tarjetas -> tarjetasTableView.setItems(FXCollections.observableArrayList(tarjetas))
        );
    }

    // === ASIGNACIONES ===

    public void mostrarPanelAsignacion() {
        cambiarPanel(asignacionPanel, "Asignación de Tableros");
        cargarTablerosAdmin();
        cargarUsuariosDisponibles();
    }

    private void cargarTablerosAdmin() {
        ejecutarTareaAsincrona(
                () -> BoardService.obtenerTablerosUsuario(),
                tableros -> {
                    tablerosComboBox.getItems().clear();
                    for (Board tablero : tableros) {
                        tablerosComboBox.getItems().add(tablero.getId() + " - " + tablero.getNombre());
                    }
                    if (!tableros.isEmpty()) {
                        tablerosComboBox.getSelectionModel().selectFirst();
                        actualizarUsuariosAsignados();
                    }
                }
        );
    }

    private void cargarUsuariosDisponibles() {
        ejecutarTareaAsincrona(
                () -> UserService.obtenerUsuariosPorRol("USER"),
                usuarios -> {
                    usuariosDisponiblesTable.getItems().clear();
                    usuariosDisponiblesTable.getItems().addAll(usuarios);
                }
        );
    }

    @FXML
    private void actualizarUsuariosAsignados() {
        String seleccion = tablerosComboBox.getValue();
        if (seleccion == null) return;

        int tableroId = Integer.parseInt(seleccion.split(" - ")[0]);
        ejecutarTareaAsincrona(
                () -> BoardService.obtenerUsuariosAsignadosATablero(tableroId),
                usuarios -> {
                    usuariosAsignadosTable.getItems().clear();
                    usuariosAsignadosTable.getItems().addAll(usuarios);
                }
        );
    }

    @FXML
    private void asignarTableroAUsuario() {
        String seleccion = tablerosComboBox.getValue();
        User usuario = usuariosDisponiblesTable.getSelectionModel().getSelectedItem();

        if (seleccion == null || usuario == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar un tablero y un usuario");
            return;
        }

        int tableroId = Integer.parseInt(seleccion.split(" - ")[0]);
        ejecutarTareaAsincrona(
                () -> BoardService.asignarTableroAUsuario(tableroId, usuario.getId()),
                resultado -> {
                    if (resultado) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tablero asignado correctamente");
                        actualizarUsuariosAsignados();
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo asignar el tablero");
                    }
                }
        );
    }

    @FXML
    private void desasignarTableroDeUsuario() {
        String seleccion = tablerosComboBox.getValue();
        User usuario = usuariosAsignadosTable.getSelectionModel().getSelectedItem();

        if (seleccion == null || usuario == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar un tablero y un usuario");
            return;
        }

        int tableroId = Integer.parseInt(seleccion.split(" - ")[0]);
        ejecutarTareaAsincrona(
                () -> BoardService.desasignarTableroDeUsuario(tableroId, usuario.getId()),
                resultado -> {
                    if (resultado) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tablero desasignado correctamente");
                        actualizarUsuariosAsignados();
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo desasignar el tablero");
                    }
                }
        );
    }

    // === MÉTODOS AUXILIARES OPTIMIZADOS ===

    private void cambiarPanel(AnchorPane panelDestino, String titulo) {
        ocultarTodosPaneles();
        headerTitleLabel.setText(titulo);
        panelDestino.setVisible(true);
        panelDestino.setManaged(true);
    }

    private <T> void ejecutarTareaAsincrona(TaskCallback<T> callback, ResultHandler<T> onSuccess) {
        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return callback.call();
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> onSuccess.handle(getValue()));
            }

            @Override
            protected void failed() {
                Platform.runLater(() ->
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error de conexión: " + getException().getMessage())
                );
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private boolean validarCamposUsuario(String email, String password, String nombre) {
        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios");
            return false;
        }

        if (!email.contains("@")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Email inválido");
            return false;
        }

        if (password.length() < 4) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "La contraseña debe tener al menos 4 caracteres");
            return false;
        }

        return true;
    }

    private int obtenerColumnaEnProceso(int tableroId) {
        List<Column> columnas = BoardDAO.obtenerColumnasPorTablero(tableroId);
        for (Column columna : columnas) {
            if ("En Proceso".equals(columna.getNombre())) {
                return columna.getId();
            }
        }

        boolean creado = BoardService.crearColumna(tableroId, "En Proceso");
        if (creado) {
            columnas = BoardDAO.obtenerColumnasPorTablero(tableroId);
            for (Column columna : columnas) {
                if ("En Proceso".equals(columna.getNombre())) {
                    return columna.getId();
                }
            }
        }
        return -1;
    }

    private void limpiarFormulario() {
        emailField.clear();
        passwordField.clear();
        nombreField.clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            Auth.cerrarSesion();
            App.app.setLoginScene(); // Usar setLoginScene() que ya existe

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            Auth.cerrarSesion();
            App.app.setLoginScene();
        }
    }

    // Interfaces funcionales para callbacks
    @FunctionalInterface
    private interface TaskCallback<T> {
        T call() throws Exception;
    }

    @FunctionalInterface
    private interface ResultHandler<T> {
        void handle(T result);
    }
}