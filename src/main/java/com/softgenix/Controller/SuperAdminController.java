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

public class SuperAdminController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField nombreField;

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
    private Button addListBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar columnas de la tabla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }

    /**
     * Método para ocultar todos los paneles
     */
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
        headerTitleLabel.setText("Gestión de Admin");
        usuariosPanel.setVisible(true);
        usuariosPanel.setManaged(true);

        // Cargar usuarios existentes al mostrar el panel
        cargarUsuarios();
    }

    @FXML
    private void crearUsuario(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String nombre = nombreField.getText();
        String rol = "ADMIN";

        System.out.println("Intentando crear admnin: " + nombre + ", " + email);

        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios");
            return;
        }

        try {
            boolean creado = UserService.crearUsuario(email, password, nombre, rol);
            System.out.println("Resultado: " + creado);

            if (creado) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Admin creado correctamente");
                limpiarFormulario();
                cargarUsuarios();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el admin");
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un admin para eliminar");
            return;
        }

        // Solo permitir eliminar usuarios tipo USER
        if (!"ADMIN".equals(usuarioSeleccionado.getRol())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Solo puede eliminar admin con rol USER");
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
        try {
            Auth.cerrarSesion();
            App.app.setLoginScene(); // Cambiar setScene por setLoginScene

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            Auth.cerrarSesion();
            App.app.setLoginScene(); // También aquí
        }
    }

    private void cargarUsuarios() {
        // Solo cargar usuarios tipo USER
        List<User> usuarios = UserService.obtenerUsuariosPorRol("ADMIN");
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
    // Elementos UI para asignación de tableros
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




    private void cargarUsuariosDisponibles() {
        List<User> usuarios = UserService.obtenerUsuariosPorRol("ADMIN");

        // Configurar columnas de la tabla
        idUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Configurar columnas de la tabla de asignados
        idAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        usuariosDisponiblesTable.getItems().clear();
        usuariosDisponiblesTable.getItems().addAll(usuarios);
    }



    // Elementos UI para tableros
    @FXML private TextField tableroNombreField;
    @FXML private TextArea tableroDescripcionField;
    @FXML private TableView<Board> tablerosTableView;
    @FXML private TableColumn<Board, Integer> tableroIdColumn;
    @FXML private TableColumn<Board, String> tableroNombreColumn;
    @FXML private TableColumn<Board, String> tableroDescripcionColumn;

    @FXML
    private void mostrarGestionTableros(ActionEvent event) {
        // Usar el método que oculta correctamente todos los paneles
        ocultarTodosPaneles();

        // Mostrar panel de tableros
        tablerosPanel.setVisible(true);
        tablerosPanel.setManaged(true);

        // Cambiar título
        headerTitleLabel.setText("Gestión de Tableros");

        // Configurar columnas si no se ha hecho antes
        if (tableroIdColumn.getCellValueFactory() == null) {
            tableroIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            tableroNombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            tableroDescripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        }

        // Cargar tableros existentes
        cargarTableros();
    }
    private void cargarTableros() {
        List<Board> tableros = BoardService.obtenerTablerosUsuario();
        tablerosTableView.setItems(FXCollections.observableArrayList(tableros));
    }

    /**
     * Crea un nuevo tablero
     */
    @FXML
    private void crearTablero() {
        String nombre = tableroNombreField.getText();
        String descripcion = tableroDescripcionField.getText();

        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El nombre del tablero es obligatorio");
            return;
        }

        try {
            // Obtener el usuario actual como propietario
            int propietarioId = Auth.getUsuarioActual().getId();
            boolean creado = BoardService.crearTablero(nombre, descripcion, propietarioId);

            if (creado) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tablero creado correctamente");
                tableroNombreField.clear();
                tableroDescripcionField.clear();
                cargarTableros(); // Recargar inmediatamente la tabla
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el tablero");
            }
        } catch (Exception e) {
            System.err.println("Error al crear tablero: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Elimina el tablero seleccionado
     */
    @FXML
    private void eliminarTablero() {
        Board tableroSeleccionado = tablerosTableView.getSelectionModel().getSelectedItem();
        if (tableroSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un tablero para eliminar");
            return;
        }

        boolean eliminado = BoardService.eliminarTablero(tableroSeleccionado.getId());
        if (eliminado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tablero eliminado correctamente");
            cargarTableros();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el tablero");
        }
    }
    // Variables para el panel de tarjetas
    @FXML private AnchorPane tarjetasPanel;
    @FXML private Label tableroActualLabel;
    @FXML private TextField tarjetaTituloField;
    @FXML private TextArea tarjetaDescripcionField;
    @FXML private TableView<Card> tarjetasTableView;
    @FXML private TableColumn<Card, String> tarjetaTituloColumn;
    @FXML private TableColumn<Card, String> tarjetaDescripcionColumn;

    // Tablero seleccionado actualmente
    private Board tableroActual;


    /**
     * Carga las tarjetas del tablero actual
     */
    private void cargarTarjetasTablero() {
        if (tableroActual == null) return;

        List<Card> tarjetas = CardDAO.obtenerTarjetasPorTablero(tableroActual.getId());
        tarjetasTableView.setItems(FXCollections.observableArrayList(tarjetas));
    }

    /**
     * Crea una nueva tarjeta en el tablero actual
     */
    @FXML
    private void crearTarjeta() {
        if (tableroActual == null) return;

        String titulo = tarjetaTituloField.getText();
        String descripcion = tarjetaDescripcionField.getText();

        if (titulo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El título de la tarjeta es obligatorio");
            return;
        }

        // Obtener o crear una columna predeterminada
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

    /**
     * Obtiene una columna predeterminada o la crea si no existe
     */
    private int obtenerColumnaPreterminada(int tableroId) {
        List<Column> columnas = BoardDAO.obtenerColumnasPorTablero(tableroId);
        if (!columnas.isEmpty()) {
            return columnas.get(0).getId();
        }

        // Si no hay columnas, crear una columna predeterminada
        boolean creado = BoardService.crearColumna(tableroId, "Por hacer");
        if (creado) {
            columnas = BoardDAO.obtenerColumnasPorTablero(tableroId);
            if (!columnas.isEmpty()) {
                return columnas.get(0).getId();
            }
        }

        return -1; // Error
    }
}
