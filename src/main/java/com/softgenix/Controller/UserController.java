package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Utils.Auth;
import com.softgenix.App.Utils.Path;
import com.softgenix.Dao.CardDAO;
import com.softgenix.Dao.ColumnDao;
import com.softgenix.Model.Board;
import com.softgenix.Model.Card;
import com.softgenix.Model.Column;
import com.softgenix.Model.User;
import com.softgenix.Service.BoardService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private Text userInitialsText;

    @FXML
    private Label headerTitleLabel;

    @FXML
    private VBox enProcesoColumn;

    @FXML
    private VBox completadaColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Obtener el usuario actual
        User currentUser = Auth.getUsuarioActual();

        // Establecer las iniciales del usuario
        if (currentUser != null && currentUser.getNombre() != null && !currentUser.getNombre().isEmpty()) {
            String nombre = currentUser.getNombre();
            String[] partes = nombre.split(" ");
            if (partes.length > 0) {
                StringBuilder iniciales = new StringBuilder();
                iniciales.append(partes[0].charAt(0));
                if (partes.length > 1) {
                    iniciales.append(partes[partes.length - 1].charAt(0));
                }
                userInitialsText.setText(iniciales.toString().toUpperCase());
            }
        }

        // Cargar el tablero asignado al usuario
        cargarTableroUsuario();
    }

    /**
     * Carga el tablero principal del usuario actual
     */
    private void cargarTableroUsuario() {
        // Obtener el ID del usuario actual
        int userId = Auth.getUsuarioActual().getId();

        // Obtener los tableros asignados al usuario (tomamos el primero)
        List<Board> tablerosAsignados = BoardService.obtenerTablerosDeUsuario(userId);

        if (tablerosAsignados == null || tablerosAsignados.isEmpty()) {
            // No hay tableros asignados
            headerTitleLabel.setText("No tienes tableros asignados");
            return;
        }

        // Tomamos el primer tablero
        Board tablero = tablerosAsignados.get(0);
        headerTitleLabel.setText("Mi tablero de " + tablero.getNombre());

        // Limpiar las columnas
        enProcesoColumn.getChildren().clear();
        completadaColumn.getChildren().clear();

        // Añadir los títulos de las columnas (ya están en el FXML)

        // Obtener columnas del tablero
        List<Column> columnas = ColumnDao.obtenerColumnasPorTablero(tablero.getId());

        if (columnas != null && columnas.size() >= 2) {
            // Cargar tarjetas de la columna "En Proceso"
            cargarTarjetasEnColumna(columnas.get(0), enProcesoColumn);

            // Cargar tarjetas de la columna "Completada"
            cargarTarjetasEnColumna(columnas.get(1), completadaColumn);
        }
    }

    /**
     * Carga las tarjetas en la columna especificada
     */
    private void cargarTarjetasEnColumna(Column columna, VBox columnContainer) {
        // Obtener las tarjetas de la columna
        List<Card> tarjetas = CardDAO.obtenerTarjetasPorColumna(columna.getId());

        if (tarjetas == null || tarjetas.isEmpty()) {
            // No hay tarjetas
            Label emptyLabel = new Label("No hay tarjetas");
            emptyLabel.getStyleClass().add("empty-message");
            columnContainer.getChildren().add(emptyLabel);
            return;
        }

        // Añadir cada tarjeta a la columna
        for (Card tarjeta : tarjetas) {
            VBox cardBox = crearTarjeta(tarjeta);
            columnContainer.getChildren().add(cardBox);
        }
    }

    /**
     * Crea una vista para una tarjeta
     */
    private VBox crearTarjeta(Card tarjeta) {
        VBox cardBox = new VBox();
        cardBox.getStyleClass().add("card");
        cardBox.setPadding(new Insets(10));
        cardBox.setSpacing(5);

        Label titleLabel = new Label(tarjeta.getTitulo());
        titleLabel.getStyleClass().add("card-title");

        Label descLabel = new Label(tarjeta.getDescripcion());
        descLabel.getStyleClass().add("card-description");
        descLabel.setWrapText(true);

        cardBox.getChildren().addAll(titleLabel, descLabel);

        return cardBox;
    }

    @FXML
    private void cerrarSesion() {
        Auth.cerrarSesion();
        App.app.setScene(Path.Login);
    }
}