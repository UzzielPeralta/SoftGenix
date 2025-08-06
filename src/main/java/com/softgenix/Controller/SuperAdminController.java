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
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SuperAdminController implements Initializable {

    private static int contadorTemporalUsuarios = -1;
    private static int contadorTemporalTableros = -1;

    private int generarIdTemporalUsuario() {
        return contadorTemporalUsuarios--;
    }
    private int generarIdTemporalTablero() {
        return contadorTemporalTableros--;
    }

    // Cache para mejorar rendimiento
    private ObservableList<User> usuariosCache;
    private ObservableList<Board> tablerosCache;
    private Map<Integer, ObservableList<Card>> tarjetasCache;
    private long ultimaActualizacionUsuarios;
    private long ultimaActualizacionTableros;
    private static final long TIEMPO_CACHE = 30000; // 30 segundos

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

    // Elementos UI para tableros
    @FXML private TextField tableroNombreField;
    @FXML private TextArea tableroDescripcionField;
    @FXML private TableView<Board> tablerosTableView;
    @FXML private TableColumn<Board, Integer> tableroIdColumn;
    @FXML private TableColumn<Board, String> tableroNombreColumn;
    @FXML private TableColumn<Board, String> tableroDescripcionColumn;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar todas las columnas una sola vez
        configurarColumnasTablas();

        // Inicializar cache
        usuariosCache = FXCollections.observableArrayList();
        tablerosCache = FXCollections.observableArrayList();
        tarjetasCache = new HashMap<>();
    }

    /**
     * Configura todas las columnas de las tablas una sola vez
     */
    private void configurarColumnasTablas() {
        // Usuarios
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));

        // Tableros
        if (tableroIdColumn != null) {
            tableroIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            tableroNombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            tableroDescripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        }

        // Tarjetas
        if (tarjetaTituloColumn != null) {
            tarjetaTituloColumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
            tarjetaDescripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        }

        // Usuarios disponibles y asignados
        if (idUsuarioColumn != null) {
            idUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nombreUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            emailUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        }

        if (idAsignadoColumn != null) {
            idAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nombreAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            emailAsignadoColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        }
    }

    /**
     * Método optimizado para ocultar todos los paneles
     */
    private void ocultarTodosPaneles() {
        AnchorPane[] paneles = {usuariosPanel, tablerosPanel, tarjetasPanel, asignacionPanel};

        for (AnchorPane panel : paneles) {
            if (panel != null) {
                panel.setVisible(false);
                panel.setManaged(false);
            }
        }

        if (welcomePanel != null) {
            welcomePanel.setVisible(false);
            welcomePanel.setManaged(false);
        }
    }

    public void mostrarGestionTableros() {
        ocultarTodosPaneles();
        headerTitleLabel.setText("Gestión de Tableros");
        tablerosPanel.setVisible(true);
        tablerosPanel.setManaged(true);
        cargarTableros();
    }

    public void mostrarGestionUsuarios() {
        ocultarTodosPaneles();
        headerTitleLabel.setText("Gestión de Admin");
        usuariosPanel.setVisible(true);
        usuariosPanel.setManaged(true);
        cargarUsuarios();
    }

    /**
     * Método optimizado para crear usuario con validación previa
     */
    @FXML
    private void crearUsuario(ActionEvent event) {
        if (!validarCamposUsuario()) return;

        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String nombre = nombreField.getText().trim();
        String rol = "ADMIN";

        // Crear usuario temporal inmediatamente
        User usuarioTemporal = new User();
        usuarioTemporal.setNombre(nombre);
        usuarioTemporal.setEmail(email);
        usuarioTemporal.setRol(rol);
        // ID temporal más pequeño
        usuarioTemporal.setId(Math.abs(email.hashCode() % 100000));

        // Agregar inmediatamente a la tabla
        usuariosCache.add(usuarioTemporal);

        // Limpiar formulario inmediatamente
        limpiarFormulario();

        Task<User> task = new Task<User>() {
            @Override
            protected User call() throws Exception {
                boolean creado = UserService.crearUsuario(email, password, nombre, rol);
                if (creado) {
                    // Obtener el usuario real
                    List<User> todosUsuarios = UserService.obtenerUsuariosPorRol("ADMIN");
                    return todosUsuarios.stream()
                            .filter(u -> u.getEmail().equals(email))
                            .reduce((first, second) -> second) // Obtener el más reciente
                            .orElse(null);
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            User usuarioReal = task.getValue();
            if (usuarioReal != null) {
                // Reemplazar temporal con real
                int index = usuariosCache.indexOf(usuarioTemporal);
                if (index >= 0) {
                    usuariosCache.set(index, usuarioReal);
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Admin creado correctamente");
            } else {
                // Si falló, remover temporal
                usuariosCache.remove(usuarioTemporal);
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el admin");
            }
        });

        task.setOnFailed(e -> {
            usuariosCache.remove(usuarioTemporal);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }
    /**
     * Validación de campos de usuario
     */
    private boolean validarCamposUsuario() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String nombre = nombreField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios");
            return false;
        }

        if (!email.contains("@")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Email inválido");
            return false;
        }

        return true;
    }

    @FXML
    private void eliminarUsuario(ActionEvent event) {
        User usuarioSeleccionado = usuariosTableView.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un admin para eliminar");
            return;
        }

        // Solo permitir eliminar usuarios tipo ADMIN
        if (!"ADMIN".equals(usuarioSeleccionado.getRol())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Solo puede eliminar admin con rol ADMIN");
            return;
        }

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return UserService.eliminarUsuario(usuarioSeleccionado.getId());
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                // Eliminar directamente del cache sin recargar toda la tabla
                usuariosCache.remove(usuarioSeleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Admin eliminado correctamente");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el admin");
            }
        });

        task.setOnFailed(e -> {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }
    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            Auth.cerrarSesion();
            App.app.setLoginScene();
        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            Auth.cerrarSesion();
            App.app.setLoginScene();
        }
    }

    /**
     * Método optimizado para cargar usuarios con cache
     */
    private void cargarUsuarios() {
        long tiempoActual = System.currentTimeMillis();

        // Usar cache si es reciente
        if (usuariosCache != null && !usuariosCache.isEmpty() &&
                (tiempoActual - ultimaActualizacionUsuarios) < TIEMPO_CACHE) {
            usuariosTableView.setItems(usuariosCache);
            return;
        }

        // Cargar de base de datos en hilo separado
        Task<List<User>> task = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return UserService.obtenerUsuariosPorRol("ADMIN");
            }
        };

        task.setOnSucceeded(e -> {
            List<User> usuarios = task.getValue();
            // Actualizar cache manteniendo la referencia de la tabla
            usuariosCache.setAll(usuarios); // Usar setAll en lugar de clear/addAll
            ultimaActualizacionUsuarios = tiempoActual;
        });

        task.setOnFailed(e -> {
            System.err.println("Error al cargar usuarios: " + task.getException().getMessage());
        });

        new Thread(task).start();
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

    /**
     * Método optimizado para cargar usuarios disponibles
     */
    private void cargarUsuariosDisponibles() {
        Task<List<User>> task = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return UserService.obtenerUsuariosPorRol("ADMIN");
            }
        };

        task.setOnSucceeded(e -> {
            List<User> usuarios = task.getValue();
            usuariosDisponiblesTable.getItems().clear();
            usuariosDisponiblesTable.getItems().addAll(usuarios);
        });

        new Thread(task).start();
    }

    @FXML
    private void mostrarGestionTableros(ActionEvent event) {
        ocultarTodosPaneles();
        tablerosPanel.setVisible(true);
        tablerosPanel.setManaged(true);
        headerTitleLabel.setText("Gestión de Tableros");
        cargarTableros();
    }

    /**
     * Método optimizado para cargar tableros con cache
     */
    private void cargarTableros() {
        long tiempoActual = System.currentTimeMillis();

        if (tablerosCache != null && !tablerosCache.isEmpty() &&
                (tiempoActual - ultimaActualizacionTableros) < TIEMPO_CACHE) {
            tablerosTableView.setItems(tablerosCache); // Corregido: era usuariosTableView
            return;
        }

        Task<List<Board>> task = new Task<List<Board>>() {
            @Override
            protected List<Board> call() throws Exception {
                return BoardService.obtenerTablerosUsuario();
            }
        };

        task.setOnSucceeded(e -> {
            List<Board> tableros = task.getValue();
            tablerosCache.setAll(tableros);
            ultimaActualizacionTableros = tiempoActual;
        });

        task.setOnFailed(e -> {
            System.err.println("Error al cargar tableros: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    /**
     * Crea un nuevo tablero optimizado
     */
    @FXML
    private void crearTablero() {
        String nombre = tableroNombreField.getText().trim();
        String descripcion = tableroDescripcionField.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El nombre del tablero es obligatorio");
            return;
        }

        // Crear tablero temporal inmediatamente para la UI
        Board tableroTemporal = new Board();
        tableroTemporal.setNombre(nombre);
        tableroTemporal.setDescripcion(descripcion);
        tableroTemporal.setPropietarioId(Auth.getUsuarioActual().getId());
        // ID temporal más pequeño usando hash
        tableroTemporal.setId(Math.abs(nombre.hashCode() % 100000));

        // Agregar inmediatamente a la tabla
        tablerosCache.add(tableroTemporal);

        // Limpiar campos inmediatamente
        tableroNombreField.clear();
        tableroDescripcionField.clear();

        // Crear en base de datos en segundo plano
        Task<Board> task = new Task<Board>() {
            @Override
            protected Board call() throws Exception {
                int propietarioId = Auth.getUsuarioActual().getId();
                boolean creado = BoardService.crearTablero(nombre, descripcion, propietarioId);
                if (creado) {
                    // Obtener el tablero real con ID correcto
                    List<Board> todosTableros = BoardService.obtenerTablerosUsuario();
                    return todosTableros.stream()
                            .filter(t -> t.getNombre().equals(nombre) && t.getPropietarioId() == propietarioId)
                            .reduce((first, second) -> second) // Obtener el más reciente
                            .orElse(null);
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Board tableroReal = task.getValue();
            if (tableroReal != null) {
                // Reemplazar el temporal con el real
                int index = tablerosCache.indexOf(tableroTemporal);
                if (index >= 0) {
                    tablerosCache.set(index, tableroReal);
                }

            } else {
                // Si falló, remover el temporal
                tablerosCache.remove(tableroTemporal);

            }
        });

        task.setOnFailed(e -> {
            // Si falló, remover el temporal
            tablerosCache.remove(tableroTemporal);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error inesperado: " + task.getException().getMessage());
        });

        new Thread(task).start();
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

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return BoardService.eliminarTablero(tableroSeleccionado.getId());
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                // Eliminar directamente del cache sin recargar
                tablerosCache.remove(tableroSeleccionado);
                tarjetasCache.remove(tableroSeleccionado.getId());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tablero eliminado correctamente");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el tablero");
            }
        });

        task.setOnFailed(e -> {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    /**
     * Carga las tarjetas del tablero actual con cache
     */
    private void cargarTarjetasTablero() {
        if (tableroActual == null) return;

        int tableroId = tableroActual.getId();

        // Verificar cache de tarjetas para este tablero
        if (tarjetasCache.containsKey(tableroId)) {
            tarjetasTableView.setItems(tarjetasCache.get(tableroId));
            return;
        }

        Task<List<Card>> task = new Task<List<Card>>() {
            @Override
            protected List<Card> call() throws Exception {
                return CardDAO.obtenerTarjetasPorTablero(tableroId);
            }
        };

        task.setOnSucceeded(e -> {
            List<Card> tarjetas = task.getValue();
            ObservableList<Card> tarjetasObservable = FXCollections.observableArrayList(tarjetas);
            tarjetasCache.put(tableroId, tarjetasObservable);
            tarjetasTableView.setItems(tarjetasObservable);
        });

        new Thread(task).start();
    }

    /**
     * Crea una nueva tarjeta en el tablero actual
     */
    @FXML
    private void crearTarjeta() {
        if (tableroActual == null) return;

        String titulo = tarjetaTituloField.getText().trim();
        String descripcion = tarjetaDescripcionField.getText().trim();

        if (titulo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El título de la tarjeta es obligatorio");
            return;
        }

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                // Obtener o crear una columna predeterminada
                int columnaId = obtenerColumnaPreterminada(tableroActual.getId());
                if (columnaId == -1) {
                    return false;
                }

                Card tarjeta = new Card();
                tarjeta.setTitulo(titulo);
                tarjeta.setDescripcion(descripcion);
                tarjeta.setColumnaId(columnaId);
                tarjeta.setCreadoPorUsuarioId(Auth.getUsuarioActual().getId());

                return CardDAO.crearTarjeta(tarjeta);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tarjeta creada correctamente");
                tarjetaTituloField.clear();
                tarjetaDescripcionField.clear();
                invalidarCacheTarjetas(tableroActual.getId());
                cargarTarjetasTablero();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear la tarjeta");
            }
        });

        new Thread(task).start();
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

    /**
     * Métodos utilitarios para invalidar cache
     */
    private void invalidarCacheUsuarios() {
        ultimaActualizacionUsuarios = 0;
        if (usuariosCache != null) {
            usuariosCache.clear();
        }
    }

    private void invalidarCacheTableros() {
        ultimaActualizacionTableros = 0;
        if (tablerosCache != null) {
            tablerosCache.clear();
        }
    }

    private void invalidarCacheTarjetas(int tableroId) {
        tarjetasCache.remove(tableroId);
    }
}