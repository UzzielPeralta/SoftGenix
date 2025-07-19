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

    /**
     * Crea una nueva tarjeta en la base de datos
     */
    public static boolean crearTarjeta(Card tarjeta) {
        String sql = "INSERT INTO TARJETAS (TITULO, DESCRIPCION, COLUMNA_ID, CREADOR_ID) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tarjeta.getTitulo());
            pstmt.setString(2, tarjeta.getDescripcion());
            pstmt.setInt(3, tarjeta.getColumnaId());
            pstmt.setInt(4, tarjeta.getCreadoPorUsuarioId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las tarjetas de una columna específica
     */
    public static List<Card> obtenerTarjetasPorColumna(int columnaId) {
        List<Card> tarjetas = new ArrayList<>();
        String sql = "SELECT ID, TITULO, DESCRIPCION, FECHA_CREACION, COLUMNA_ID, CREADOR_ID FROM TARJETAS WHERE COLUMNA_ID = ? ORDER BY ID";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, columnaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Card tarjeta = new Card();
                    tarjeta.setId(rs.getInt("ID"));
                    tarjeta.setTitulo(rs.getString("TITULO"));
                    tarjeta.setDescripcion(rs.getString("DESCRIPCION"));

                    Timestamp timestamp = rs.getTimestamp("FECHA_CREACION");
                    if (timestamp != null) {
                        tarjeta.setFechaCreacion(timestamp.toLocalDateTime());
                    }

                    tarjeta.setColumnaId(rs.getInt("COLUMNA_ID"));
                    tarjeta.setCreadoPorUsuarioId(rs.getInt("CREADOR_ID"));

                    tarjetas.add(tarjeta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tarjetas;
    }

    /**
     * Elimina una tarjeta por su ID
     */
    public static boolean eliminarTarjeta(int tarjetaId) {
        String sql = "DELETE FROM TARJETAS WHERE ID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tarjetaId);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza la columna de una tarjeta (mover tarjeta)
     */
    public static boolean actualizarColumnaTarjeta(int tarjetaId, int nuevaColumnaId) {
        String sql = "UPDATE TARJETAS SET COLUMNA_ID = ? WHERE ID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevaColumnaId);
            pstmt.setInt(2, tarjetaId);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene una tarjeta por su ID
     */
    public static Card obtenerTarjetaPorId(int tarjetaId) {
        String sql = "SELECT ID, TITULO, DESCRIPCION, FECHA_CREACION, COLUMNA_ID, CREADOR_ID FROM TARJETAS WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tarjetaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Card tarjeta = new Card();
                    tarjeta.setId(rs.getInt("ID"));
                    tarjeta.setTitulo(rs.getString("TITULO"));
                    tarjeta.setDescripcion(rs.getString("DESCRIPCION"));

                    Timestamp timestamp = rs.getTimestamp("FECHA_CREACION");
                    if (timestamp != null) {
                        tarjeta.setFechaCreacion(timestamp.toLocalDateTime());
                    }

                    tarjeta.setColumnaId(rs.getInt("COLUMNA_ID"));
                    tarjeta.setCreadoPorUsuarioId(rs.getInt("CREADOR_ID"));

                    return tarjeta;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     * Obtiene todas las tarjetas asociadas a un tablero específico
     * @param tableroId ID del tablero
     * @return Lista de tarjetas del tablero
     */
    public static List<Card> obtenerTarjetasPorTablero(int tableroId) {
        List<Card> tarjetas = new ArrayList<>();

        // Primero obtenemos todas las columnas del tablero
        List<Column> columnas = ColumnDao.obtenerColumnasPorTablero(tableroId);

        // Para cada columna, obtenemos sus tarjetas y las añadimos a la lista
        for (Column columna : columnas) {
            tarjetas.addAll(obtenerTarjetasPorColumna(columna.getId()));
        }

        return tarjetas;
    }
}