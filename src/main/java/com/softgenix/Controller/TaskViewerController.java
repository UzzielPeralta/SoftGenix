package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Utils.Auth;
import com.softgenix.Model.Board;
import com.softgenix.Service.BoardService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.softgenix.Dao.CardDAO;
import com.softgenix.Dao.ColumnDao;
import com.softgenix.Model.Card;
import com.softgenix.Model.Column;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TaskViewerController implements Initializable {

    @FXML private Label nombreUsuarioLabel;
    @FXML private ListView<String> tablerosListView;
    @FXML private Label tableroActualLabel;
    @FXML private Text inicialesText;
    @FXML private ScrollPane tableroScrollPane;
    @FXML private HBox columnasContainer;


    private int ultimoTableroMostrado = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarUsuario();
        cargarTablerosAsignados();
        configurarSeleccionTablero();
    }

    private void configurarUsuario() {
        String nombre = Auth.getUsuarioActual().getNombre();
        nombreUsuarioLabel.setText("Bienvenido/a " + nombre);

        if (nombre != null && !nombre.isEmpty()) {
            inicialesText.setText(nombre.substring(0, 1).toUpperCase());
        }
    }

    private void configurarSeleccionTablero() {
        tablerosListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        mostrarTableroSeleccionado(newVal);
                    }
                });
    }

    private void cargarTablerosAsignados() {
        Task<List<Board>> task = new Task<List<Board>>() {
            @Override
            protected List<Board> call() throws Exception {
                return BoardService.obtenerTablerosAsignadosAUsuario(Auth.getUsuarioActual().getId());
            }

            @Override
            protected void succeeded() {
                List<Board> tableros = getValue();
                tablerosListView.getItems().clear();

                for (Board tablero : tableros) {
                    tablerosListView.getItems().add(tablero.getId() + " - " + tablero.getNombre());
                }
            }
        };

        new Thread(task).start();
    }

    private void mostrarTableroSeleccionado(String tableroStr) {
        int tableroId = Integer.parseInt(tableroStr.split(" - ")[0]);

        // Evitar recargar el mismo tablero
        if (ultimoTableroMostrado == tableroId) return;
        ultimoTableroMostrado = tableroId;

        String nombreTablero = tableroStr.substring(tableroStr.indexOf(" - ") + 3);
        tableroActualLabel.setText(nombreTablero);

        cargarColumnasYTarjetas(tableroId);
    }

    private void cargarColumnasYTarjetas(int tableroId) {
        columnasContainer.getChildren().clear();

        Task<List<Column>> task = new Task<List<Column>>() {
            @Override
            protected List<Column> call() throws Exception {
                return ColumnDao.obtenerColumnasPorTablero(tableroId);
            }

            @Override
            protected void succeeded() {
                List<Column> columnas = getValue();

                if (columnas == null || columnas.isEmpty()) {
                    Label noColumnasLabel = new Label("Este tablero no tiene columnas");
                    columnasContainer.getChildren().add(noColumnasLabel);
                    return;
                }

                for (Column columna : columnas) {
                    VBox columnaBox = crearVistaColumna(columna);
                    columnasContainer.getChildren().add(columnaBox);
                }
            }
        };

        new Thread(task).start();
    }

    private VBox crearVistaColumna(Column columna) {
        VBox columnaBox = new VBox();
        columnaBox.getStyleClass().add("column");
        columnaBox.setPrefWidth(300);
        columnaBox.setSpacing(10);
        columnaBox.setPadding(new Insets(10));

        Label tituloColumna = new Label(columna.getNombre());
        tituloColumna.getStyleClass().add("column-title");
        columnaBox.getChildren().add(tituloColumna);

        // Cargar tarjetas
        cargarTarjetasColumna(columna, columnaBox);

        return columnaBox;
    }

    private void cargarTarjetasColumna(Column columna, VBox columnaBox) {
        Task<List<Card>> task = new Task<List<Card>>() {
            @Override
            protected List<Card> call() throws Exception {
                return CardDAO.obtenerTarjetasPorColumna(columna.getId());
            }

            @Override
            protected void succeeded() {
                List<Card> tarjetas = getValue();

                if (tarjetas == null || tarjetas.isEmpty()) {
                    Label emptyLabel = new Label("No hay tarjetas");
                    emptyLabel.getStyleClass().add("empty-message");
                    columnaBox.getChildren().add(emptyLabel);
                } else {
                    for (Card tarjeta : tarjetas) {
                        VBox tarjetaBox = crearVistaTarjeta(tarjeta);
                        columnaBox.getChildren().add(tarjetaBox);
                    }
                }
            }
        };

        new Thread(task).start();
    }

    private VBox crearVistaTarjeta(Card tarjeta) {
        VBox tarjetaBox = new VBox();
        tarjetaBox.getStyleClass().add("card");
        tarjetaBox.setPadding(new Insets(10));
        tarjetaBox.setSpacing(5);

        Label titleLabel = new Label(tarjeta.getTitulo());
        titleLabel.getStyleClass().add("card-title");

        Label descLabel = new Label(tarjeta.getDescripcion());
        descLabel.getStyleClass().add("card-description");
        descLabel.setWrapText(true);

        tarjetaBox.getChildren().addAll(titleLabel, descLabel);

        return tarjetaBox;
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
}