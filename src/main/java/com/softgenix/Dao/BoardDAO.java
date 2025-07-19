package com.softgenix.Dao;

import com.softgenix.App.Config.Database;
import com.softgenix.Model.Board;
import com.softgenix.Model.Column;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {

    /**
     * Crea un nuevo tablero en la base de datos
     */
    public static boolean crearTablero(Board tablero) {
        String sql = "INSERT INTO TABLEROS (NOMBRE, PROPIETARIO_ID) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tablero.getNombre());
            pstmt.setInt(2, tablero.getPropietarioId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene un tablero por su ID
     */
    public static Board obtenerTableroPorId(int tableroId) {
        String sql = "SELECT ID, NOMBRE, PROPIETARIO_ID FROM TABLEROS WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Board tablero = new Board();
                    tablero.setId(rs.getInt("ID"));
                    tablero.setNombre(rs.getString("NOMBRE"));
                    tablero.setPropietarioId(rs.getInt("PROPIETARIO_ID"));
                    return tablero;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene el orden máximo de columnas en un tablero
     */
    public static int obtenerOrdenMaximoColumnas(int tableroId) {
        String sql = "SELECT MAX(ORDEN) AS MAX_ORDEN FROM COLUMNAS WHERE TABLERO_ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("MAX_ORDEN");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Si no hay columnas, empezamos en 0
    }

    /**
     * Crea una nueva columna en un tablero
     */
    public static boolean crearColumna(Column columna) {
        String sql = "INSERT INTO COLUMNAS (NOMBRE, ORDEN, TABLERO_ID) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, columna.getNombre());
            pstmt.setInt(2, columna.getOrden());
            pstmt.setInt(3, columna.getTableroId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todos los tableros de un usuario
     */
    public static List<Board> obtenerTablerosPorUsuario(int usuarioId) {
        List<Board> tableros = new ArrayList<>();
        String sql = "SELECT ID, NOMBRE, PROPIETARIO_ID FROM TABLEROS WHERE PROPIETARIO_ID = ? ORDER BY ID";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Board tablero = new Board();
                    tablero.setId(rs.getInt("ID"));
                    tablero.setNombre(rs.getString("NOMBRE"));
                    tablero.setPropietarioId(rs.getInt("PROPIETARIO_ID"));
                    tableros.add(tablero);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tableros;
    }

    /**
     * Obtiene todas las columnas de un tablero
     */
    public static List<Column> obtenerColumnasPorTablero(int tableroId) {
        List<Column> columnas = new ArrayList<>();
        String sql = "SELECT ID, NOMBRE, ORDEN, TABLERO_ID FROM COLUMNAS WHERE TABLERO_ID = ? ORDER BY ORDEN";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Column columna = new Column();
                    columna.setId(rs.getInt("ID"));
                    columna.setNombre(rs.getString("NOMBRE"));
                    columna.setOrden(rs.getInt("ORDEN"));
                    columna.setTableroId(rs.getInt("TABLERO_ID"));
                    columnas.add(columna);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columnas;
    }

    /**
     * Elimina un tablero y todas sus columnas y tarjetas asociadas
     */
    public static boolean eliminarTablero(int tableroId) {
        // Primero eliminamos las asignaciones de tarjetas
        String sqlAsignaciones = "DELETE FROM ASIGNACIONES_TARJETA WHERE TARJETA_ID IN " +
                "(SELECT ID FROM TARJETAS WHERE COLUMNA_ID IN " +
                "(SELECT ID FROM COLUMNAS WHERE TABLERO_ID = ?))";

        // Luego eliminamos las tarjetas
        String sqlTarjetas = "DELETE FROM TARJETAS WHERE COLUMNA_ID IN " +
                "(SELECT ID FROM COLUMNAS WHERE TABLERO_ID = ?)";

        // Después eliminamos las columnas
        String sqlColumnas = "DELETE FROM COLUMNAS WHERE TABLERO_ID = ?";

        // Finalmente eliminamos el tablero
        String sqlTablero = "DELETE FROM TABLEROS WHERE ID = ?";

        try (Connection conn = Database.getConnection()) {
            // Iniciamos una transacción para asegurar que todo se elimina o nada
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtAsignaciones = conn.prepareStatement(sqlAsignaciones);
                 PreparedStatement pstmtTarjetas = conn.prepareStatement(sqlTarjetas);
                 PreparedStatement pstmtColumnas = conn.prepareStatement(sqlColumnas);
                 PreparedStatement pstmtTablero = conn.prepareStatement(sqlTablero)) {

                pstmtAsignaciones.setInt(1, tableroId);
                pstmtAsignaciones.executeUpdate();

                pstmtTarjetas.setInt(1, tableroId);
                pstmtTarjetas.executeUpdate();

                pstmtColumnas.setInt(1, tableroId);
                pstmtColumnas.executeUpdate();

                pstmtTablero.setInt(1, tableroId);
                int filasAfectadas = pstmtTablero.executeUpdate();

                conn.commit();
                return filasAfectadas > 0;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}