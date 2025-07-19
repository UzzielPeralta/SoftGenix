package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Utils.Auth;
import com.softgenix.App.Utils.Path;
import com.softgenix.Model.Board;
import com.softgenix.Service.BoardService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TaskViewerController implements Initializable {

    @FXML
    private Label nombreUsuarioLabel;

    @FXML
    private ListView<String> tablerosListView;

    @FXML
    private Label tableroActualLabel;

    @FXML
    private Text inicialesText;

    @FXML
    private ScrollPane tableroScrollPane;

    @FXML
    private HBox columnasContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mostrar nombre del usuario actual
        nombreUsuarioLabel.setText("Bienvenido/a " + Auth.getUsuarioActual().getNombre());

        // Configurar iniciales del usuario
        String nombre = Auth.getUsuarioActual().getNombre();
        if (nombre != null && !nombre.isEmpty()) {
            inicialesText.setText(nombre.substring(0, 1).toUpperCase());
        }

        // Cargar tableros asignados al usuario
        cargarTablerosAsignados();

        // Configurar listener para selección de tableros
        tablerosListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> mostrarTableroSeleccionado(newVal));
    }

    private void cargarTablerosAsignados() {
        int usuarioId = Auth.getUsuarioActual().getId();
        List<Board> tableros = BoardService.obtenerTablerosDeUsuario(usuarioId);

        tablerosListView.getItems().clear();
        for (Board tablero : tableros) {
            tablerosListView.getItems().add(tablero.getId() + " - " + tablero.getNombre());
        }
    }

    private void mostrarTableroSeleccionado(String tableroStr) {
        if (tableroStr == null) return;

        // Extraer ID del tablero del string seleccionado
        int tableroId = Integer.parseInt(tableroStr.split(" - ")[0]);
        String nombreTablero = tableroStr.substring(tableroStr.indexOf(" - ") + 3);

        // Actualizar título
        tableroActualLabel.setText(nombreTablero);

        // Aquí iría la lógica para cargar las columnas y tarjetas del tablero
        cargarColumnasYTarjetas(tableroId);
    }

    private void cargarColumnasYTarjetas(int tableroId) {
        // Limpiar contenedor de columnas
        columnasContainer.getChildren().clear();

        // Aquí implementarías la lógica para cargar las columnas y tarjetas
        // Por ejemplo:
        // List<Column> columnas = ColumnService.obtenerColumnasPorTablero(tableroId);
        // para cada columna, crear un VBox con sus tarjetas...
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Auth.cerrarSesion();
        App.app.setScene(Path.Login);
    }
}