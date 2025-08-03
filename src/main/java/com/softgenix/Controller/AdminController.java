package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Utils.Auth;
import com.softgenix.App.Utils.Path;
import com.softgenix.Dao.BoardDAO;
import com.softgenix.Dao.CardDAO;
import com.softgenix.Model.Board;
import com.softgenix.Model.Card;
import com.softgenix.Model.Column;
import com.softgenix.Model.User;
import com.softgenix.Service.BoardService;
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

public class AdminController implements Initializable {

    @FXML private TableView<User> usuariosTableView;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nombreColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> rolColumn;
    @FXML private VBox welcomePanel;
    @FXML private AnchorPane usuariosPanel;
    @FXML private AnchorPane tablerosPanel;
    @FXML private Label headerTitleLabel;
    @FXML private Button addListBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }

    private void ocultarTodosPaneles() {
        welcomePanel.setVisible(false);
        welcomePanel.setManaged(false);
        usuariosPanel.setVisible(false);
        usuariosPanel.setManaged(false);
        tablerosPanel.setVisible(false);
        tablerosPanel.setManaged(false);
        tarjetasPanel.setVisible(false);
        tarjetasPanel.setManaged(false);
        asignacionPanel.setVisible(false);
        asignacionPanel.setManaged(false);
    }

    public void mostrarGestionTableros() {
        ocultarTodosPaneles();
        headerTitleLabel.setText("Gestión de Tableros");
        tablerosPanel.setVisible(true);
        tablerosPanel.setManaged(true);
    }

    public void mostrarGestionUsuarios() {
        ocultarTodosPaneles();
        headerTitleLabel.setText("Gestión de Usuarios");
        usuariosPanel.setVisible(true);
        usuariosPanel.setManaged(true);
    }

    public void mostrarPanelAsignacion() {
        ocultarTodosPaneles();
        headerTitleLabel.setText("Asignación de Tableros");
        asignacionPanel.setVisible(true);
        asignacionPanel.setManaged(true);
        cargarTablerosAdmin();
        cargarUsuariosDisponibles();
    }

    public void mostrarGestionTarjetas() {
        ocultarTodosPaneles();
        headerTitleLabel.setText("Gestión de Tarjetas");
        tarjetasPanel.setVisible(true);
        tarjetasPanel.setManaged(true);
    }

    @FXML
    private void volverATableros() {
        ocultarTodosPaneles();
        tablerosPanel.setVisible(true);
        tablerosPanel.setManaged(true);
        headerTitleLabel.setText("Gestión de Tableros");
        cargarTableros();
    }

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nombreField;

    @FXML
    private void crearUsuario(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String nombre = nombreField.getText();
        String rol = "USER";

        System.out.println("Intentando crear usuario: " + nombre + ", " + email);

        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios");
            return;
        }

        try {
            boolean creado = UserService.crearUsuario(email, password, nombre, rol);
            System.out.println("Resultado: " + creado);

            if (creado) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario creado correctamente");
                limpiarFormulario();
                cargarUsuarios();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el usuario");
            }
        } catch (Exception e) {
            System.err.println("Excepción: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarUsuario(ActionEvent event) {
        User usuarioSeleccionado = usuariosTableView.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un usuario para eliminar");
            return;
        }

        if (!"USER".equals(usuarioSeleccionado.getRol())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Solo puede eliminar usuarios con rol USER");
            return;
        }

        boolean eliminado = UserService.eliminarUsuario(usuarioSeleccionado.getId());
        if (eliminado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario eliminado correctamente");
            cargarUsuarios();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el usuario");
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Auth.cerrarSesion();
        App.app.setScene(Path.Login);
    }

    private void cargarUsuarios() {
        List<User> usuarios = UserService.obtenerUsuariosPorRol("USER");
        usuariosTableView.setItems(FXCollections.observableArrayList(usuarios));
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

    @FXML private AnchorPane asignacionPanel;
    @FXML private ComboBox<String> tablerosComboBox;
    @FXML private TableView<User> usuariosDisponiblesTable;
    @FXML private TableColumn<User, Integer> idUsuarioColumn;
    @FXML private TableColumn<User, String> nombreUsuarioColumn;
    @FXML private TableColumn<User, String> emailUsuarioColumn;
    @FXML private TableView<User> usuariosAsignadosTable;
    @FXML private TableColumn<User, Integer> idAsignadoColumn;
    @FXML private TableColumn<User, String> nombreAsignadoColumn;
    @FXML private TableColumn<User, String> emailAsignadoColumn;
    @FXML private Button asignarTablerosBtn;

    private void cargarTablerosAdmin() {
        List<Board> tableros = BoardService.obtenerTablerosUsuario();
        tablerosComboBox.getItems().clear();

        for (Board tablero : tableros) {
            tablerosComboBox.getItems().add(tablero.getId() + " - " + tablero.getNombre());
        }

        if (!tableros.isEmpty()) {
            tablerosComboBox.getSelectionModel().selectFirst();
            actualizarUsuariosAsignados();
        }
    }

    private void cargarUsuariosDisponibles() {
        List<User> usuarios = UserService.obtenerUsuariosPorRol("USER");

        idUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        idAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        usuariosDisponiblesTable.getItems().clear();
        usuariosDisponiblesTable.getItems().addAll(usuarios);
    }

    @FXML
    private void actualizarUsuariosAsignados() {
        String seleccion = tablerosComboBox.getValue();
        if (seleccion == null) return;

        int tableroId = Integer.parseInt(seleccion.split(" - ")[0]);
        List<User> usuariosAsignados = BoardService.obtenerUsuariosAsignadosATablero(tableroId);

        usuariosAsignadosTable.getItems().clear();
        usuariosAsignadosTable.getItems().addAll(usuariosAsignados);
    }

    @FXML
    private void asignarTableroAUsuario() {
        String seleccion = tablerosComboBox.getValue();
        User usuarioSeleccionado = usuariosDisponiblesTable.getSelectionModel().getSelectedItem();

        if (seleccion == null || usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar un tablero y un usuario");
            return;
        }

        int tableroId = Integer.parseInt(seleccion.split(" - ")[0]);
        int usuarioId = usuarioSeleccionado.getId();

        boolean resultado = BoardService.asignarTableroAUsuario(tableroId, usuarioId);

        if (resultado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tablero asignado correctamente");
            actualizarUsuariosAsignados();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo asignar el tablero al usuario");
        }
    }

    @FXML
    private void desasignarTableroDeUsuario() {
        String seleccion = tablerosComboBox.getValue();
        User usuarioSeleccionado = usuariosAsignadosTable.getSelectionModel().getSelectedItem();

        if (seleccion == null || usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar un tablero y un usuario asignado");
            return;
        }

        int tableroId = Integer.parseInt(seleccion.split(" - ")[0]);
        int usuarioId = usuarioSeleccionado.getId();

        boolean resultado = BoardService.desasignarTableroDeUsuario(tableroId, usuarioId);

        if (resultado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Asignación eliminada correctamente");
            actualizarUsuariosAsignados();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la asignación");
        }
    }

    @FXML private TableView<Board> tablerosTableView;
    @FXML private TableColumn<Board, Integer> tableroIdColumn;
    @FXML private TableColumn<Board, String> tableroNombreColumn;
    @FXML private TableColumn<Board, String> tableroDescripcionColumn;

    @FXML
    private void mostrarGestionTableros(ActionEvent event) {
        ocultarTodosPaneles();
        tablerosPanel.setVisible(true);
        tablerosPanel.setManaged(true);
        headerTitleLabel.setText("Gestión de Tableros");

        if (tableroIdColumn.getCellValueFactory() == null) {
            tableroIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            tableroNombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            tableroDescripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        }

        cargarTableros();
    }

    private void cargarTableros() {
        List<Board> tableros = BoardService.obtenerTablerosUsuario();
        tablerosTableView.setItems(FXCollections.observableArrayList(tableros));
    }

    @FXML private AnchorPane tarjetasPanel;
    @FXML private Label tableroActualLabel;
    @FXML private TextField tarjetaTituloField;
    @FXML private TextArea tarjetaDescripcionField;
    @FXML private TableView<Card> tarjetasTableView;
    @FXML private TableColumn<Card, String> tarjetaTituloColumn;
    @FXML private TableColumn<Card, String> tarjetaDescripcionColumn;

    private Board tableroActual;

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
        tarjetaTituloColumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tarjetaDescripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        cargarTarjetasTablero();
        tarjetasPanel.setVisible(true);
        tarjetasPanel.setManaged(true);
    }

    private void cargarTarjetasTablero() {
        if (tableroActual == null) return;
        List<Card> tarjetas = CardDAO.obtenerTarjetasPorTablero(tableroActual.getId());
        tarjetasTableView.setItems(FXCollections.observableArrayList(tarjetas));
    }

    private int obtenerColumnaPreterminada(int tableroId) {
        List<Column> columnas = BoardDAO.obtenerColumnasPorTablero(tableroId);
        if (!columnas.isEmpty()) {
            return columnas.get(0).getId();
        }

        boolean creado = BoardService.crearColumna(tableroId, "Por hacer");
        if (creado) {
            columnas = BoardDAO.obtenerColumnasPorTablero(tableroId);
            if (!columnas.isEmpty()) {
                return columnas.get(0).getId();
            }
        }
        return -1;
    }

    @FXML
    private void crearTarjeta() {
        if (tableroActual == null) return;
        String titulo = tarjetaTituloField.getText();
        String descripcion = tarjetaDescripcionField.getText();

        if (titulo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El título de la tarjeta es obligatorio");
            return;
        }

        int columnaId = obtenerColumnaPreterminada(tableroActual.getId());
        if (columnaId == -1) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear una columna para la tarjeta");
            return;
        }

        Card tarjeta = new Card();
        tarjeta.setTitulo(titulo);
        tarjeta.setDescripcion(descripcion);
        tarjeta.setColumnaId(columnaId);
        tarjeta.setCreadoPorUsuarioId(Auth.getUsuarioActual().getId());

        boolean creado = CardDAO.crearTarjeta(tarjeta);

        if (creado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tarjeta creada correctamente");
            tarjetaTituloField.clear();
            tarjetaDescripcionField.clear();
            cargarTarjetasTablero();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear la tarjeta");
        }
    }

    @FXML
    private void eliminarTarjeta() {
        Card tarjetaSeleccionada = tarjetasTableView.getSelectionModel().getSelectedItem();
        if (tarjetaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione una tarjeta para eliminar");
            return;
        }

        boolean eliminado = CardDAO.eliminarTarjeta(tarjetaSeleccionada.getId());
        if (eliminado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tarjeta eliminada correctamente");
            cargarTarjetasTablero();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la tarjeta");
        }
    }
}
