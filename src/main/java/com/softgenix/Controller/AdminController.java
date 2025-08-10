package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Utils.Auth;
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

public class AdminController implements Initializable {
    // Agregar al inicio de la clase AdminController
    private ObservableList<User> usuariosCache = FXCollections.observableArrayList();
    private ObservableList<Board> tablerosCache = FXCollections.observableArrayList();
    private Map<Integer, List<User>> usuariosAsignadosCache = new HashMap<>();
    private long ultimaActualizacionUsuarios = 0;
    private long ultimaActualizacionTableros = 0;
    private static final long TIEMPO_CACHE = 30000; // 30 segundos

    // Columnas de tarjetas
    @FXML
    private TableColumn<Card, String> tarjetaProcesoColumn;

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
    private AnchorPane tarjetasPanel;
    @FXML
    private AnchorPane asignacionPanel;
    @FXML
    private Label headerTitleLabel;

    // Formulario usuarios
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nombreField;

    // Tableros
    @FXML
    private TableView<Board> tablerosTableView;
    @FXML
    private TableColumn<Board, Integer> tableroIdColumn;
    @FXML
    private TableColumn<Board, String> tableroNombreColumn;
    @FXML
    private TableColumn<Board, String> tableroDescripcionColumn;

    // Asignaciones
    @FXML
    private ComboBox<String> tablerosComboBox;
    @FXML
    private TableView<User> usuariosDisponiblesTable;
    @FXML
    private TableColumn<User, Integer> idUsuarioColumn;
    @FXML
    private TableColumn<User, String> nombreUsuarioColumn;
    @FXML
    private TableColumn<User, String> emailUsuarioColumn;
    @FXML
    private TableView<User> usuariosAsignadosTable;
    @FXML
    private TableColumn<User, Integer> idAsignadoColumn;
    @FXML
    private TableColumn<User, String> nombreAsignadoColumn;
    @FXML
    private TableColumn<User, String> emailAsignadoColumn;

    // Tarjetas
    @FXML
    private Label tableroActualLabel;
    @FXML
    private TextField tarjetaTituloField;
    @FXML
    private TextArea tarjetaDescripcionField;
    @FXML
    private TableView<Card> tarjetasTableView;
    @FXML
    private TableColumn<Card, String> tarjetaTituloColumn;
    @FXML
    private TableColumn<Card, String> tarjetaDescripcionColumn;

    private Board tableroActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTodasLasColumnas();

        // Pre-cargar datos inmediatamente
        precargarDatosIniciales();
    }

    /**
     * Pre-carga todos los datos necesarios en segundo plano
     */
    private void precargarDatosIniciales() {
        Task<Void> preloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Cargar usuarios en paralelo
                    Thread usuariosThread = new Thread(() -> {
                        try {
                            List<User> usuarios = UserService.obtenerUsuariosPorRol("USER");
                            Platform.runLater(() -> {
                                usuariosCache.setAll(usuarios);
                                ultimaActualizacionUsuarios = System.currentTimeMillis();
                            });
                        } catch (Exception e) {
                            System.err.println("Error pre-cargando usuarios: " + e.getMessage());
                        }
                    });

                    // Cargar tableros en paralelo
                    Thread tablerosThread = new Thread(() -> {
                        try {
                            List<Board> tableros = BoardService.obtenerTablerosUsuario();
                            Platform.runLater(() -> {
                                tablerosCache.setAll(tableros);
                                ultimaActualizacionTableros = System.currentTimeMillis();
                            });
                        } catch (Exception e) {
                            System.err.println("Error pre-cargando tableros: " + e.getMessage());
                        }
                    });

                    usuariosThread.start();
                    tablerosThread.start();

                    usuariosThread.join();
                    tablerosThread.join();

                } catch (Exception e) {
                    System.err.println("Error en pre-carga: " + e.getMessage());
                }
                return null;
            }
        };

        Thread preloadThread = new Thread(preloadTask);
        preloadThread.setDaemon(true);
        preloadThread.start();
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

        // Nueva columna para mostrar el proceso/estado
        tarjetaProcesoColumn.setCellValueFactory(cellData -> {
            Card tarjeta = cellData.getValue();
            String nombreColumna = obtenerNombreColumnaPorId(tarjeta.getColumnaId());
            return new javafx.beans.property.SimpleStringProperty(nombreColumna);
        });

        tarjetaProcesoColumn.setCellFactory(column -> new TableCell<Card, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String icono = obtenerIconoPorEstado(item);
                    String color = obtenerColorPorEstado(item);

                    setText(icono + " " + item);
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });
    }


    private String obtenerIconoPorEstado(String estado) {
        if (estado == null) return "❓";

        String estadoLimpio = estado.toLowerCase().trim();

        // Patrones para completado
        if (estadoLimpio.contains("completado") ||
                estadoLimpio.contains("completadas") || // AGREGADO
                estadoLimpio.contains("done") ||
                estadoLimpio.contains("terminado") ||
                estadoLimpio.contains("terminadas") || // AGREGADO
                estadoLimpio.contains("complete")) {
            return "✅"; // Palomita verde
        }

        // Patrones para en proceso
        if (estadoLimpio.contains("proceso") ||
                estadoLimpio.contains("progreso") ||
                estadoLimpio.contains("doing") ||
                estadoLimpio.contains("progress")) {
            return "🔄"; // Flecha circular
        }

        // Patrones para pendiente
        if (estadoLimpio.contains("pendiente") ||
                estadoLimpio.contains("pendientes") || // AGREGADO
                estadoLimpio.contains("to do") ||
                estadoLimpio.contains("todo") ||
                estadoLimpio.contains("por hacer") ||
                estadoLimpio.contains("backlog")) {
            return "⏸️"; // Pausa
        }

        // Patrones para cancelado
        if (estadoLimpio.contains("cancelado") ||
                estadoLimpio.contains("canceladas") || // AGREGADO
                estadoLimpio.contains("cancelled") ||
                estadoLimpio.contains("canceled")) {
            return "❌"; // X roja
        }

        // Patrones para revisión
        if (estadoLimpio.contains("revision") ||
                estadoLimpio.contains("review") ||
                estadoLimpio.contains("testing")) {
            return "👁️"; // Ojo
        }
        return "❓";
    }

    private String obtenerColorPorEstado(String estado) {
        if (estado == null) return "#6c757d";

        String estadoLimpio = estado.toLowerCase().trim();

        // Patrones para completado
        if (estadoLimpio.contains("completado") ||
                estadoLimpio.contains("completadas") || // AGREGADO
                estadoLimpio.contains("done") ||
                estadoLimpio.contains("terminado") ||
                estadoLimpio.contains("terminadas") || // AGREGADO
                estadoLimpio.contains("complete")) {
            return "#28a745"; // Verde
        }

        // Patrones para en proceso
        if (estadoLimpio.contains("proceso") ||
                estadoLimpio.contains("progreso") ||
                estadoLimpio.contains("doing") ||
                estadoLimpio.contains("progress")) {
            return "#ffc107"; // Amarillo/Naranja
        }

        // Patrones para pendiente
        if (estadoLimpio.contains("pendiente") ||
                estadoLimpio.contains("pendientes") || // AGREGADO
                estadoLimpio.contains("to do") ||
                estadoLimpio.contains("todo") ||
                estadoLimpio.contains("por hacer") ||
                estadoLimpio.contains("backlog")) {
            return "#dc3545"; // Rojo
        }

        // Patrones para cancelado
        if (estadoLimpio.contains("cancelado") ||
                estadoLimpio.contains("canceladas") || // AGREGADO
                estadoLimpio.contains("cancelled") ||
                estadoLimpio.contains("canceled")) {
            return "#6c757d"; // Gris
        }

        // Patrones para revisión
        if (estadoLimpio.contains("revision") ||
                estadoLimpio.contains("review") ||
                estadoLimpio.contains("testing")) {
            return "#17a2b8"; // Azul
        }

        return "#6c757d"; // Gris por defecto
    }



    // Método para obtener el nombre de la columna por ID
    private final java.util.Map<Integer, String> columnasCache = new java.util.HashMap<>();

    private String obtenerNombreColumnaPorId(int columnaId) {
        if (tableroActual == null) return "Sin asignar";

        // Usar caché para evitar consultas repetidas
        if (columnasCache.containsKey(columnaId)) {
            return columnasCache.get(columnaId);
        }

        try {
            List<Column> columnas = BoardDAO.obtenerColumnasPorTablero(tableroActual.getId());

            // Llenar caché
            for (Column col : columnas) {
                columnasCache.put(col.getId(), col.getNombre());
            }

            return columnasCache.getOrDefault(columnaId, "Desconocido");
        } catch (Exception e) {
            System.err.println("Error obteniendo nombre de columna: " + e.getMessage());
            return "Error";
        }
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

        String tituloOriginal = headerTitleLabel.getText();
        headerTitleLabel.setText("✓ Creando usuario...");

        // Limpiar formulario inmediatamente para mejor UX
        limpiarFormulario();

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return UserService.crearUsuario(email, password, nombre, "USER");
            }
        };

        task.setOnSucceeded(e -> {
            boolean creado = task.getValue();
            Platform.runLater(() -> {
                if (creado) {
                    headerTitleLabel.setText("✓ Usuario creado exitosamente");

                    // RECARGA SILENCIOSA - mantiene datos actuales mientras carga
                    recargarUsuariosSilenciosamente();

                    restaurarTituloTrasDelay(tituloOriginal, 2000);
                } else {
                    headerTitleLabel.setText("✗ Error al crear usuario");
                    restaurarTituloTrasDelay(tituloOriginal, 3000);
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                headerTitleLabel.setText("✗ Error de conexión");
                restaurarTituloTrasDelay(tituloOriginal, 3000);
            });
        });

        new Thread(task).start();
    }

    /**
     * Recarga usuarios desde la base de datos SIN limpiar la tabla actual
     */
    private void recargarUsuariosSilenciosamente() {
        Task<List<User>> task = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return UserService.obtenerUsuariosPorRol("USER");
            }
        };

        task.setOnSucceeded(e -> {
            List<User> usuariosNuevos = task.getValue();
            Platform.runLater(() -> {
                // Solo actualizar si realmente hay cambios
                if (!usuariosNuevos.equals(usuariosCache)) {
                    usuariosCache.setAll(usuariosNuevos);
                    ultimaActualizacionUsuarios = System.currentTimeMillis();
                    // La tabla se actualiza automáticamente porque está vinculada al ObservableList
                }
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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
        confirmacion.setContentText("Usuario: " + usuarioSeleccionado.getNombre() +
                "\nEsta acción no se puede deshacer.");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        // ELIMINACIÓN OPTIMISTA: Remover inmediatamente de la UI
        int posicionOriginal = usuariosCache.indexOf(usuarioSeleccionado);
        usuariosCache.remove(usuarioSeleccionado);

        // Mostrar feedback inmediato
        String tituloOriginal = headerTitleLabel.getText();
        headerTitleLabel.setText("✓ Usuario eliminado: " + usuarioSeleccionado.getNombre());

        // Eliminar de la base de datos en segundo plano
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return UserDAO.eliminarUsuario(usuarioSeleccionado.getId());
            }
        };

        task.setOnSucceeded(e -> {
            boolean eliminado = task.getValue();
            Platform.runLater(() -> {
                if (eliminado) {
                    // Éxito
                    restaurarTituloTrasDelay(tituloOriginal, 2000);
                } else {
                    // Error - restaurar usuario
                    if (posicionOriginal >= 0) {
                        usuariosCache.add(posicionOriginal, usuarioSeleccionado);
                    } else {
                        usuariosCache.add(usuarioSeleccionado);
                    }
                    headerTitleLabel.setText("✗ Error al eliminar usuario");
                    restaurarTituloTrasDelay(tituloOriginal, 3000);
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                // Restaurar usuario
                if (posicionOriginal >= 0) {
                    usuariosCache.add(posicionOriginal, usuarioSeleccionado);
                } else {
                    usuariosCache.add(usuarioSeleccionado);
                }
                headerTitleLabel.setText("✗ Error de conexión");
                restaurarTituloTrasDelay(tituloOriginal, 3000);
            });
        });

        new Thread(task).start();
    }

    private void cargarUsuarios() {
        // SIEMPRE mostrar datos existentes primero (si los hay)
        if (!usuariosCache.isEmpty()) {
            usuariosTableView.setItems(usuariosCache);

            // Si el caché es reciente, no recargar
            if ((System.currentTimeMillis() - ultimaActualizacionUsuarios) < TIEMPO_CACHE) {
                return;
            }
        }

        // Si no hay datos, mostrar loading en el header
        if (usuariosCache.isEmpty()) {
            headerTitleLabel.setText("Cargando usuarios...");
        }

        // Cargar/actualizar datos en segundo plano
        Task<List<User>> task = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return UserService.obtenerUsuariosPorRol("USER");
            }
        };

        task.setOnSucceeded(e -> {
            List<User> usuariosNuevos = task.getValue();
            Platform.runLater(() -> {
                usuariosCache.setAll(usuariosNuevos);
                ultimaActualizacionUsuarios = System.currentTimeMillis();
                usuariosTableView.setItems(usuariosCache);

                // Solo restaurar título si estaba en "Cargando..."
                if (headerTitleLabel.getText().contains("Cargando")) {
                    headerTitleLabel.setText("Gestión de Usuario");
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                if (headerTitleLabel.getText().contains("Cargando")) {
                    headerTitleLabel.setText("Error cargando usuarios");
                    restaurarTituloTrasDelay("Gestión de Usuario", 3000);
                }
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    // === GESTIÓN DE TABLEROS ===

    @FXML
    private void mostrarGestionTableros(ActionEvent event) {
        cambiarPanel(tablerosPanel, "Gestión de Tableros");
        cargarTableros();
    }

    private void cargarTableros() {
        // Si ya tenemos datos en caché, usarlos inmediatamente
        if (!tablerosCache.isEmpty()) {
            tablerosTableView.setItems(tablerosCache);

            // Si el caché es reciente, no recargar
            if ((System.currentTimeMillis() - ultimaActualizacionTableros) < TIEMPO_CACHE) {
                return;
            }
        }

        // Actualizar en segundo plano
        Task<List<Board>> task = new Task<List<Board>>() {
            @Override
            protected List<Board> call() throws Exception {
                return BoardService.obtenerTablerosUsuario();
            }
        };

        task.setOnSucceeded(e -> {
            List<Board> tableros = task.getValue();
            tablerosCache.setAll(tableros);
            ultimaActualizacionTableros = System.currentTimeMillis();
            tablerosTableView.setItems(tablerosCache);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Eliminar tablero con eliminación optimista
     */
    @FXML
    private void eliminarTablero() {
        Board tableroSeleccionado = tablerosTableView.getSelectionModel().getSelectedItem();
        if (tableroSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un tablero para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este tablero?");
        confirmacion.setContentText("Tablero: " + tableroSeleccionado.getNombre() +
                "\n⚠️ Se eliminarán todas las tarjetas y columnas asociadas.");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        // ELIMINACIÓN OPTIMISTA
        int posicionOriginal = tablerosCache.indexOf(tableroSeleccionado);
        tablerosCache.remove(tableroSeleccionado);

        // Limpiar caché relacionado
        usuariosAsignadosCache.remove(tableroSeleccionado.getId());

        String tituloOriginal = headerTitleLabel.getText();
        headerTitleLabel.setText("✓ Tablero eliminado: " + tableroSeleccionado.getNombre());

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return BoardService.eliminarTablero(tableroSeleccionado.getId());
            }
        };

        task.setOnSucceeded(e -> {
            boolean eliminado = task.getValue();
            Platform.runLater(() -> {
                if (eliminado) {
                    restaurarTituloTrasDelay(tituloOriginal, 2000);
                } else {
                    // Restaurar tablero
                    if (posicionOriginal >= 0) {
                        tablerosCache.add(posicionOriginal, tableroSeleccionado);
                    } else {
                        tablerosCache.add(tableroSeleccionado);
                    }
                    headerTitleLabel.setText("✗ Error al eliminar tablero");
                    restaurarTituloTrasDelay(tituloOriginal, 3000);
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                // Restaurar tablero
                if (posicionOriginal >= 0) {
                    tablerosCache.add(posicionOriginal, tableroSeleccionado);
                } else {
                    tablerosCache.add(tableroSeleccionado);
                }
                headerTitleLabel.setText("✗ Error de conexión");
                restaurarTituloTrasDelay(tituloOriginal, 3000);
            });
        });

        new Thread(task).start();
    }

    // === GESTIÓN DE TARJETAS ===

    @FXML
    private void gestionarTarjetasTablero() {
        tableroActual = tablerosTableView.getSelectionModel().getSelectedItem();
        if (tableroActual == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un tablero para gestionar sus tarjetas");
            return;
        }


        columnasCache.clear();

        tablerosPanel.setVisible(false);
        tablerosPanel.setManaged(false);
        tableroActualLabel.setText("Cargando tablero de " + tableroActual.getNombre() + "...");
        cargarTarjetasTablero();
        tarjetasPanel.setVisible(true);
        tarjetasPanel.setManaged(true);
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

        // Crear tarjeta temporal para UI inmediata
        Card tarjetaTemporal = new Card();
        tarjetaTemporal.setId(-1); // ID temporal
        tarjetaTemporal.setTitulo(titulo);
        tarjetaTemporal.setDescripcion(descripcion);
        tarjetaTemporal.setColumnaId(columnaId);
        tarjetaTemporal.setCreadoPorUsuarioId(Auth.getUsuarioActual().getId());

        // Agregar inmediatamente a la UI
        tarjetasTableView.getItems().add(0, tarjetaTemporal);

        // Limpiar formulario inmediatamente
        tarjetaTituloField.clear();
        tarjetaDescripcionField.clear();

        String tituloOriginal = headerTitleLabel.getText();
        headerTitleLabel.setText("✓ Tarjeta creada: " + titulo);

        // Crear en base de datos
        Card tarjeta = new Card();
        tarjeta.setTitulo(titulo);
        tarjeta.setDescripcion(descripcion);
        tarjeta.setColumnaId(columnaId);
        tarjeta.setCreadoPorUsuarioId(Auth.getUsuarioActual().getId());

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return CardDAO.crearTarjeta(tarjeta);
            }
        };

        task.setOnSucceeded(e -> {
            boolean creada = task.getValue();
            Platform.runLater(() -> {
                if (creada) {
                    // Éxito - recargar para obtener IDs reales
                    cargarTarjetasTablero();
                    restaurarTituloTrasDelay(tituloOriginal, 2000);
                } else {
                    // Error - remover tarjeta temporal
                    tarjetasTableView.getItems().remove(tarjetaTemporal);
                    headerTitleLabel.setText("✗ Error al crear tarjeta");
                    restaurarTituloTrasDelay(tituloOriginal, 3000);
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                tarjetasTableView.getItems().remove(tarjetaTemporal);
                headerTitleLabel.setText("✗ Error de conexión");
                restaurarTituloTrasDelay(tituloOriginal, 3000);
            });
        });

        new Thread(task).start();
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
                () -> {
                    List<Card> tarjetas = CardDAO.obtenerTarjetasPorTablero(tableroActual.getId());
                    return tarjetas;
                },
                tarjetas -> {
                    tarjetasTableView.setItems(FXCollections.observableArrayList(tarjetas));

                    long completadas = tarjetas.stream()
                            .filter(t -> "Completado".equals(obtenerNombreColumnaPorId(t.getColumnaId())))
                            .count();

                    tableroActualLabel.setText(String.format(
                            "Tablero: %s (%d tarjetas, %d completadas)",
                            tableroActual.getNombre(),
                            tarjetas.size(),
                            completadas
                    ));
                }
        );
    }

    // === ASIGNACIONES ===

    public void mostrarPanelAsignacion() {
        cambiarPanel(asignacionPanel, "Asignación de Tableros");
        cargarTablerosAdmin();
        cargarUsuariosDisponibles();
    }

    private void cargarTablerosAdmin() {
        // Usar caché de tableros
        if (!tablerosCache.isEmpty()) {
            actualizarComboBoxTableros();
            return;
        }

        Task<List<Board>> task = new Task<List<Board>>() {
            @Override
            protected List<Board> call() throws Exception {
                return BoardService.obtenerTablerosUsuario();
            }
        };

        task.setOnSucceeded(e -> {
            tablerosCache.setAll(task.getValue());
            actualizarComboBoxTableros();
        });

        new Thread(task).start();
    }

    private void actualizarComboBoxTableros() {
        tablerosComboBox.getItems().clear();
        for (Board tablero : tablerosCache) {
            // SIN MOSTRAR ID - Solo nombre
            tablerosComboBox.getItems().add(tablero.getNombre());
        }
        if (!tablerosCache.isEmpty()) {
            tablerosComboBox.getSelectionModel().selectFirst();
            actualizarUsuariosAsignados();
        }
    }





    @FXML
    private void asignarTableroAUsuario() {
        String seleccion = tablerosComboBox.getValue();
        User usuario = usuariosDisponiblesTable.getSelectionModel().getSelectedItem();

        if (seleccion == null || usuario == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar un tablero y un usuario");
            return;
        }

        // Buscar tablero por nombre
        Board tableroSeleccionado = tablerosCache.stream()
                .filter(b -> b.getNombre().equals(seleccion))
                .findFirst()
                .orElse(null);

        if (tableroSeleccionado == null) return;

        int tableroId = tableroSeleccionado.getId();

        // ASIGNACIÓN OPTIMISTA
        if (usuariosAsignadosCache.containsKey(tableroId)) {
            usuariosAsignadosCache.get(tableroId).add(usuario);
            usuariosAsignadosTable.getItems().add(usuario);
        }

        String tituloOriginal = headerTitleLabel.getText();
        headerTitleLabel.setText("✓ Tablero asignado a " + usuario.getNombre());

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return BoardService.asignarTableroAUsuario(tableroId, usuario.getId());
            }
        };

        task.setOnSucceeded(e -> {
            boolean asignado = task.getValue();
            Platform.runLater(() -> {
                if (asignado) {
                    restaurarTituloTrasDelay(tituloOriginal, 2000);
                } else {
                    // Revertir asignación optimista
                    if (usuariosAsignadosCache.containsKey(tableroId)) {
                        usuariosAsignadosCache.get(tableroId).remove(usuario);
                        usuariosAsignadosTable.getItems().remove(usuario);
                    }
                    headerTitleLabel.setText("✗ Error al asignar tablero");
                    restaurarTituloTrasDelay(tituloOriginal, 3000);
                }
            });
        });

        new Thread(task).start();
    }

    private void cargarUsuariosDisponibles() {
        // Verificar si necesitamos recargar
        boolean necesitaRecarga = usuariosCache.isEmpty() ||
                (System.currentTimeMillis() - ultimaActualizacionUsuarios) > TIEMPO_CACHE;

        if (!necesitaRecarga) {
            usuariosDisponiblesTable.setItems(usuariosCache);
            return;
        }

        // Cargar datos frescos
        Task<List<User>> task = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return UserService.obtenerUsuariosPorRol("USER");
            }
        };

        task.setOnSucceeded(e -> {
            usuariosCache.setAll(task.getValue());
            ultimaActualizacionUsuarios = System.currentTimeMillis();
            usuariosDisponiblesTable.setItems(usuariosCache);
        });

        new Thread(task).start();
    }

    @FXML
    private void actualizarUsuariosAsignados() {
        String seleccion = tablerosComboBox.getValue();
        if (seleccion == null) return;

        // Buscar tablero por nombre
        Board tableroSeleccionado = tablerosCache.stream()
                .filter(b -> b.getNombre().equals(seleccion))
                .findFirst()
                .orElse(null);

        if (tableroSeleccionado == null) return;

        int tableroId = tableroSeleccionado.getId();

        // Usar caché si está disponible
        if (usuariosAsignadosCache.containsKey(tableroId)) {
            usuariosAsignadosTable.setItems(FXCollections.observableArrayList(usuariosAsignadosCache.get(tableroId)));
            return;
        }

        Task<List<User>> task = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return BoardService.obtenerUsuariosAsignadosATablero(tableroId);
            }
        };

        task.setOnSucceeded(e -> {
            List<User> usuarios = task.getValue();
            usuariosAsignadosCache.put(tableroId, usuarios);
            usuariosAsignadosTable.setItems(FXCollections.observableArrayList(usuarios));
        });

        new Thread(task).start();
    }

    // === MÉTODOS AUXILIARES ===

    private void cambiarPanel(AnchorPane panelDestino, String titulo) {
        ocultarTodosPaneles();
        headerTitleLabel.setText(titulo);
        panelDestino.setVisible(true);
        panelDestino.setManaged(true);
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
    private void desasignarTableroDeUsuario() {
        String seleccion = tablerosComboBox.getValue();
        User usuario = usuariosAsignadosTable.getSelectionModel().getSelectedItem();

        if (seleccion == null || usuario == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar un tablero y un usuario");
            return;
        }

        // Buscar tablero por nombre
        Board tableroSeleccionado = tablerosCache.stream()
                .filter(b -> b.getNombre().equals(seleccion))
                .findFirst()
                .orElse(null);

        if (tableroSeleccionado == null) return;

        int tableroId = tableroSeleccionado.getId();

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar desasignación");
        confirmacion.setHeaderText("¿Está seguro de desasignar este tablero?");
        confirmacion.setContentText("Usuario: " + usuario.getNombre() +
                "\nTablero: " + tableroSeleccionado.getNombre());

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        // DESASIGNACIÓN OPTIMISTA
        if (usuariosAsignadosCache.containsKey(tableroId)) {
            usuariosAsignadosCache.get(tableroId).remove(usuario);
            usuariosAsignadosTable.getItems().remove(usuario);
        }

        String tituloOriginal = headerTitleLabel.getText();
        headerTitleLabel.setText("✓ Tablero desasignado de " + usuario.getNombre());

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return BoardService.desasignarTableroDeUsuario(tableroId, usuario.getId());
            }
        };

        task.setOnSucceeded(e -> {
            boolean desasignado = task.getValue();
            Platform.runLater(() -> {
                if (desasignado) {
                    restaurarTituloTrasDelay(tituloOriginal, 2000);
                } else {
                    // Restaurar usuario si falló
                    if (usuariosAsignadosCache.containsKey(tableroId)) {
                        usuariosAsignadosCache.get(tableroId).add(usuario);
                        usuariosAsignadosTable.getItems().add(usuario);
                    }
                    headerTitleLabel.setText("✗ Error al desasignar");
                    restaurarTituloTrasDelay(tituloOriginal, 3000);
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                // Restaurar usuario
                if (usuariosAsignadosCache.containsKey(tableroId)) {
                    usuariosAsignadosCache.get(tableroId).add(usuario);
                    usuariosAsignadosTable.getItems().add(usuario);
                }
                headerTitleLabel.setText("✗ Error de conexión");
                restaurarTituloTrasDelay(tituloOriginal, 3000);
            });
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




    private void debugColumnNames() {
        if (tableroActual == null) return;

        try {
            List<Column> columnas = BoardDAO.obtenerColumnasPorTablero(tableroActual.getId());
            System.out.println("Columnas encontradas para el tablero " + tableroActual.getId() + ":");
            for (Column col : columnas) {
                System.out.println("- ID: " + col.getId() + ", Nombre: '" + col.getNombre() + "'");
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo columnas: " + e.getMessage());

        }
    }
    /**
     * Restaura el título del header después de un delay
     */
    private void restaurarTituloTrasDelay(String tituloOriginal, int delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                Platform.runLater(() -> headerTitleLabel.setText(tituloOriginal));
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @FunctionalInterface
    interface TaskCallback<T> {
        T call() throws Exception;
    }

    @FunctionalInterface
    interface ResultHandler<T> {
        void handle(T result);
    }

    /**
     * Ejecuta una tarea asíncrona y maneja el resultado en el hilo de JavaFX
     */
    private <T> void ejecutarTareaAsincrona(TaskCallback<T> tarea, ResultHandler<T> callback) {
        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return tarea.call();
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> callback.handle(task.getValue())));

        task.setOnFailed(e -> Platform.runLater(() -> {
            System.err.println("Error en tarea asíncrona: " + task.getException().getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error de conexión");
        }));

        new Thread(task).start();
    }
}