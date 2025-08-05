package com.softgenix.Controller;

import com.softgenix.App.App;
import com.softgenix.App.Config.Database;
import com.softgenix.App.Utils.Auth;
import com.softgenix.Dao.CardDAO;
import com.softgenix.Model.Board;
import com.softgenix.Model.Card;
import com.softgenix.Model.Column;
import com.softgenix.Service.BoardService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

    // Cache para mejorar rendimiento
    private Column columnaEnProcesoCache;
    private Column columnaCompletadasCache;
    private Board tableroActual;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar iniciales del usuario
        configurarIniciales();

        // Cargar tablero de forma optimizada
        cargarTableroUsuario();
    }

    /**
     * Configura las iniciales del usuario
     */
    private void configurarIniciales() {
        var usuario = Auth.getUsuarioActual();
        if (usuario != null && usuario.getNombre() != null && !usuario.getNombre().isEmpty()) {
            String iniciales = usuario.getNombre().substring(0, 1).toUpperCase();
            userInitialsText.setText(iniciales);
        }
    }

    /**
     * Carga el tablero principal del usuario actual - OPTIMIZADO
     */
    private void cargarTableroUsuario() {
        int userId = Auth.getUsuarioActual().getId();
        List<Board> tablerosAsignados = BoardService.obtenerTablerosAsignadosAUsuario(userId);

        if (tablerosAsignados == null || tablerosAsignados.isEmpty()) {
            mostrarMensajeVacio();
            return;
        }

        tableroActual = tablerosAsignados.get(0);
        headerTitleLabel.setText("Mi tablero de " + tableroActual.getNombre());

        limpiarColumnas();

        // Obtener columnas de forma optimizada
        List<Column> columnas = obtenerColumnasOptimizado(tableroActual.getId());

        if (columnas != null && !columnas.isEmpty()) {
            // Buscar y cachear columnas importantes
            identificarColumnasImportantes(columnas);

            // Cargar tarjetas en paralelo
            cargarTarjetasOptimizado();
        }
    }

    /**
     * Obtiene columnas de forma optimizada (sin logs de debug)
     */
    private List<Column> obtenerColumnasOptimizado(int tableroId) {
        List<Column> columnas = new ArrayList<>();
        String sql = "SELECT ID, NOMBRE, TABLERO_ID, ORDEN FROM COLUMNAS WHERE TABLERO_ID = ? ORDER BY ORDEN";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Column columna = new Column();
                    columna.setId(rs.getInt("ID"));
                    columna.setNombre(rs.getString("NOMBRE"));
                    columna.setTableroId(rs.getInt("TABLERO_ID"));
                    columna.setOrden(rs.getInt("ORDEN"));
                    columnas.add(columna);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener columnas: " + e.getMessage());
        }

        return columnas;
    }

    /**
     * Identifica y cachea las columnas importantes
     */
    private void identificarColumnasImportantes(List<Column> columnas) {
        columnaEnProcesoCache = null;
        columnaCompletadasCache = null;

        for (Column columna : columnas) {
            String nombre = columna.getNombre().toLowerCase().trim();

            if ((nombre.contains("proceso") || nombre.contains("pendiente")) && columnaEnProcesoCache == null) {
                columnaEnProcesoCache = columna;
            }

            if ((nombre.contains("completad") || nombre.contains("terminad") || nombre.contains("done"))
                    && columnaCompletadasCache == null) {
                columnaCompletadasCache = columna;
            }
        }
    }

    /**
     * Carga tarjetas de forma optimizada
     */
    private void cargarTarjetasOptimizado() {
        if (columnaEnProcesoCache != null) {
            cargarTarjetasEnColumna(columnaEnProcesoCache, enProcesoColumn, true);
        }

        if (columnaCompletadasCache != null) {
            cargarTarjetasEnColumna(columnaCompletadasCache, completadaColumn, false);
        }
    }

    /**
     * Limpia las columnas manteniendo solo los títulos
     */
    private void limpiarColumnas() {
        if (enProcesoColumn.getChildren().size() > 1) {
            enProcesoColumn.getChildren().removeIf(node -> !(node instanceof Label) ||
                    !((Label) node).getText().equals("En Proceso"));
        }

        if (completadaColumn.getChildren().size() > 1) {
            completadaColumn.getChildren().removeIf(node -> !(node instanceof Label) ||
                    !((Label) node).getText().equals("Completadas"));
        }
    }

    /**
     * Carga las tarjetas en la columna especificada - OPTIMIZADO
     */
    private void cargarTarjetasEnColumna(Column columna, VBox columnContainer, boolean esColumnaEnProceso) {
        List<Card> tarjetas = CardDAO.obtenerTarjetasPorColumna(columna.getId());

        if (tarjetas == null || tarjetas.isEmpty()) {
            return;
        }

        for (Card tarjeta : tarjetas) {
            VBox cardView = crearTarjeta(tarjeta, esColumnaEnProceso);
            columnContainer.getChildren().add(cardView);
        }
    }

    /**
     * Crea la vista de una tarjeta - OPTIMIZADO
     */
    private VBox crearTarjeta(Card tarjeta, boolean esColumnaEnProceso) {
        VBox cardBox = new VBox();
        cardBox.getStyleClass().add("card");
        cardBox.setSpacing(5); // Espaciado entre elementos

        // Título de la tarjeta
        Label titleLabel = new Label(tarjeta.getTitulo());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);

        // Descripción de la tarjeta
        Label descLabel = new Label(tarjeta.getDescripcion());
        descLabel.getStyleClass().add("card-description");
        descLabel.setWrapText(true);

        cardBox.getChildren().addAll(titleLabel, descLabel);

        // Botón de acción con estilos corregidos
        String textoBoton = esColumnaEnProceso ? "Marcar como Completada" : "Mover a En Proceso";
        Button accionBtn = new Button(textoBoton);

        // Aplicar estilos específicos según el tipo de botón
        if (esColumnaEnProceso) {
            accionBtn.getStyleClass().addAll("btn", "complete-btn");
            accionBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 5 10;");
        } else {
            accionBtn.getStyleClass().addAll("btn", "process-btn");
            accionBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 5 10;");
        }

        accionBtn.setOnAction(e -> moverTarjetaOptimizado(tarjeta, esColumnaEnProceso, cardBox));

        cardBox.getChildren().add(accionBtn);

        return cardBox;
    }

    /**
     * Mueve tarjeta de forma optimizada - SIN LOGS DE DEBUG
     */
    private void moverTarjetaOptimizado(Card tarjeta, boolean esColumnaEnProceso, VBox cardView) {
        try {
            // Determinar columna destino
            Column columnaDestino = esColumnaEnProceso ? columnaCompletadasCache : columnaEnProcesoCache;

            if (columnaDestino == null) {
                mostrarError("No se pudo encontrar la columna destino");
                return;
            }

            // Actualizar en base de datos de forma eficiente
            boolean actualizado = actualizarTarjetaRapido(tarjeta.getId(), columnaDestino.getId());

            if (actualizado) {
                // Actualizar la vista de forma inmediata
                actualizarVistaInmediata(tarjeta, cardView, esColumnaEnProceso);
            } else {
                mostrarError("No se pudo mover la tarjeta");
            }

        } catch (Exception e) {
            System.err.println("Error al mover tarjeta: " + e.getMessage());
            mostrarError("Error inesperado al mover la tarjeta");
        }
    }

    /**
     * Actualiza tarjeta en BD de forma rápida (una sola query)
     */
    private boolean actualizarTarjetaRapido(int tarjetaId, int nuevaColumnaId) {
        String sql = "UPDATE TARJETAS SET COLUMNA_ID = ? WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevaColumnaId);
            pstmt.setInt(2, tarjetaId);

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error al actualizar tarjeta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza la vista inmediatamente sin recargar todo
     */
    private  void actualizarVistaInmediata(Card tarjeta, VBox cardView, boolean eraColumnaEnProceso) {
        // Remover de columna actual
        VBox columnaOrigen = eraColumnaEnProceso ? enProcesoColumn : completadaColumn;
        VBox columnaDestino = eraColumnaEnProceso ? completadaColumn : enProcesoColumn;

        columnaOrigen.getChildren().remove(cardView);

        // Recrear tarjeta para columna destino con estilos correctos
        VBox nuevaCardView = crearTarjeta(tarjeta, !eraColumnaEnProceso);

        // Asegurar que los estilos se apliquen correctamente
        nuevaCardView.applyCss();

        columnaDestino.getChildren().add(nuevaCardView);

        // Mostrar feedback mínimo
        String mensaje = eraColumnaEnProceso ? "Tarea completada ✓" : "Tarea en proceso ↻";
        mostrarFeedbackRapido(mensaje);
    }

    /**
     * Muestra feedback rápido sin bloquear UI
     */
    private void mostrarFeedbackRapido(String mensaje) {
        // Cambiar temporalmente el título para dar feedback
        String tituloOriginal = headerTitleLabel.getText();
        headerTitleLabel.setText(mensaje + " ✓");

        // Restaurar título después de 1 segundo
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(() ->
                        headerTitleLabel.setText(tituloOriginal)
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Muestra mensaje cuando no hay tableros asignados
     */
    private void mostrarMensajeVacio() {
        headerTitleLabel.setText("Sin tableros asignados");

        Label emptyEnProceso = new Label("No hay tableros asignados");
        emptyEnProceso.getStyleClass().add("empty-message");
        enProcesoColumn.getChildren().add(emptyEnProceso);

        Label emptyCompletadas = new Label("No hay tableros asignados");
        emptyCompletadas.getStyleClass().add("empty-message");
        completadaColumn.getChildren().add(emptyCompletadas);
    }

    /**
     * Muestra mensaje de error de forma rápida
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void cerrarSesion() {
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