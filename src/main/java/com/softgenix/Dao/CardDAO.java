package com.softgenix.Dao;

import com.softgenix.App.Config.Database;
import com.softgenix.Model.Card;
import com.softgenix.Model.Column;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {

    // Constantes SQL
    private static final String CREAR_TARJETA_SQL = "INSERT INTO TARJETAS (TITULO, DESCRIPCION, COLUMNA_ID, CREADOR_ID) VALUES (?, ?, ?, ?)";
    private static final String OBTENER_TARJETAS_COLUMNA_SQL = "SELECT ID, TITULO, DESCRIPCION, FECHA_CREACION, COLUMNA_ID, CREADOR_ID FROM TARJETAS WHERE COLUMNA_ID = ? ORDER BY ID";
    private static final String ELIMINAR_TARJETA_SQL = "DELETE FROM TARJETAS WHERE ID = ?";
    private static final String ACTUALIZAR_COLUMNA_SQL = "UPDATE TARJETAS SET COLUMNA_ID = ? WHERE ID = ?";

    /**
     * Método helper para mapear ResultSet a Card
     */
    private static Card mapearTarjeta(ResultSet rs) throws SQLException {
        Card tarjeta = new Card();
        tarjeta.setId(rs.getInt("ID"));
        tarjeta.setTitulo(rs.getString("TITULO"));
        tarjeta.setDescripcion(rs.getString("DESCRIPCION"));
        tarjeta.setColumnaId(rs.getInt("COLUMNA_ID"));
        tarjeta.setCreadoPorUsuarioId(rs.getInt("CREADOR_ID"));

        Timestamp timestamp = rs.getTimestamp("FECHA_CREACION");
        if (timestamp != null) {
            tarjeta.setFechaCreacion(timestamp.toLocalDateTime());
        }

        return tarjeta;
    }

    /**
     * Optimización del método obtenerTarjetasPorColumna
     */
    public static List<Card> obtenerTarjetasPorColumna(int columnaId) {
        List<Card> tarjetas = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_TARJETAS_COLUMNA_SQL)) {

            pstmt.setInt(1, columnaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tarjetas.add(mapearTarjeta(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tarjetas por columna: " + e.getMessage());
        }

        return tarjetas;
    }

    /**
     * Optimización del método obtenerTarjetasPorTablero con una sola consulta SQL
     */
    public static List<Card> obtenerTarjetasPorTablero(int tableroId) {
        List<Card> tarjetas = new ArrayList<>();
        String sql = "SELECT t.ID, t.TITULO, t.DESCRIPCION, t.FECHA_CREACION, t.COLUMNA_ID, t.CREADOR_ID " +
                "FROM TARJETAS t " +
                "JOIN COLUMNAS c ON t.COLUMNA_ID = c.ID " +
                "WHERE c.TABLERO_ID = ? ORDER BY t.ID";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tarjetas.add(mapearTarjeta(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tarjetas por tablero: " + e.getMessage());
        }

        return tarjetas;
    }

    // Los demás métodos permanecen igual pero con mejor manejo de errores
    public static boolean crearTarjeta(Card tarjeta) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(CREAR_TARJETA_SQL)) {

            pstmt.setString(1, tarjeta.getTitulo());
            pstmt.setString(2, tarjeta.getDescripcion());
            pstmt.setInt(3, tarjeta.getColumnaId());
            pstmt.setInt(4, tarjeta.getCreadoPorUsuarioId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear tarjeta: " + e.getMessage());
            return false;
        }
    }

    public static boolean eliminarTarjeta(int tarjetaId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ELIMINAR_TARJETA_SQL)) {

            pstmt.setInt(1, tarjetaId);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar tarjeta: " + e.getMessage());
            return false;
        }
    }

    public static boolean actualizarColumnaTarjeta(int tarjetaId, int nuevaColumnaId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ACTUALIZAR_COLUMNA_SQL)) {

            pstmt.setInt(1, nuevaColumnaId);
            pstmt.setInt(2, tarjetaId);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar columna de tarjeta: " + e.getMessage());
            return false;
        }
    }
}