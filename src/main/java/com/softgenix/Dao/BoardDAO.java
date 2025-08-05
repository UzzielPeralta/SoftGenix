package com.softgenix.Dao;

import com.softgenix.App.Config.Database;
import com.softgenix.App.Utils.Auth;
import com.softgenix.Model.Board;
import com.softgenix.Model.Column;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {

    // Pool de conexiones reutilizable
    private static final String CREAR_TABLERO_SQL = "INSERT INTO TABLEROS (NOMBRE, PROPIETARIO_ID, DESCRIPCION) VALUES (?, ?, ?)";
    private static final String OBTENER_TABLERO_SQL = "SELECT ID, NOMBRE, PROPIETARIO_ID FROM TABLEROS WHERE ID = ?";
    private static final String OBTENER_TODOS_TABLEROS_SQL = "SELECT ID, NOMBRE, PROPIETARIO_ID, DESCRIPCION FROM TABLEROS ORDER BY ID DESC";
    private static final String ELIMINAR_TABLERO_SQL = "DELETE FROM TABLEROS WHERE ID = ?";

    /**
     * Crea un nuevo tablero en la base de datos
     */
    public static int crearTableroConId(String nombre, String descripcion, int propietarioId) {
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(CREAR_TABLERO_SQL, new String[]{"ID"})) {

            stmt.setString(1, nombre);
            stmt.setInt(2, propietarioId);
            stmt.setString(3, descripcion);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Error al crear tablero: " + e.getMessage());
            return -1;
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
    public static Board obtenerUltimoTableroCreado() {
        String sql = "SELECT * FROM TABLEROS WHERE PROPIETARIO_ID = ? ORDER BY ID DESC FETCH FIRST 1 ROWS ONLY";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Auth.getUsuarioActual().getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Board tablero = new Board();
                    tablero.setId(rs.getInt("ID"));
                    tablero.setNombre(rs.getString("NOMBRE"));
                    try {
                        tablero.setDescripcion(rs.getString("DESCRIPCION"));
                    } catch (SQLException e) {
                        tablero.setDescripcion("");
                    }
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
     * Obtiene todos los tableros de un usuario
     */
    public static List<Board> obtenerTablerosUsuario() {
        List<Board> tableros = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(OBTENER_TODOS_TABLEROS_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Board tablero = mapearBoard(rs);
                tableros.add(tablero);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tableros: " + e.getMessage());
        }

        return tableros;
    }

    private static Board mapearBoard(ResultSet rs) throws SQLException {
        Board tablero = new Board();
        tablero.setId(rs.getInt("ID"));
        tablero.setNombre(rs.getString("NOMBRE"));
        tablero.setPropietarioId(rs.getInt("PROPIETARIO_ID"));

        try {
            tablero.setDescripcion(rs.getString("DESCRIPCION"));
        } catch (SQLException e) {
            tablero.setDescripcion(""); // Valor por defecto
        }

        return tablero;
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
     /**



    /**
     * Elimina un tablero y todas sus columnas y tarjetas asociadas
     */
    public static boolean eliminarTablero(int tableroId) {
        String[] sqlStatements = {
                "DELETE FROM ASIGNACIONES_TARJETA WHERE TARJETA_ID IN (SELECT ID FROM TARJETAS WHERE COLUMNA_ID IN (SELECT ID FROM COLUMNAS WHERE TABLERO_ID = ?))",
                "DELETE FROM TARJETAS WHERE COLUMNA_ID IN (SELECT ID FROM COLUMNAS WHERE TABLERO_ID = ?)",
                "DELETE FROM COLUMNAS WHERE TABLERO_ID = ?",
                "DELETE FROM ASIGNACIONES_TABLERO WHERE TABLERO_ID = ?",
                ELIMINAR_TABLERO_SQL
        };

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            for (String sql : sqlStatements) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, tableroId);
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar tablero: " + e.getMessage());
            return false;
        }
    }
}