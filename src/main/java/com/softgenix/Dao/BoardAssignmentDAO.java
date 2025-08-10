package com.softgenix.Dao;

import com.softgenix.App.Config.Database;
import com.softgenix.Model.Board;
import com.softgenix.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BoardAssignmentDAO {

    // Constantes SQL
    private static final String ASIGNAR_TABLERO_SQL = "INSERT INTO ASIGNACIONES_TABLERO (TABLERO_ID, USUARIO_ID) VALUES (?, ?)";
    private static final String DESASIGNAR_TABLERO_SQL = "DELETE FROM ASIGNACIONES_TABLERO WHERE TABLERO_ID = ? AND USUARIO_ID = ?";
    private static final String OBTENER_TABLEROS_ASIGNADOS_SQL =
            "SELECT t.* FROM TABLEROS t JOIN ASIGNACIONES_TABLERO a ON t.ID = a.TABLERO_ID WHERE a.USUARIO_ID = ?";
    private static final String OBTENER_USUARIOS_ASIGNADOS_SQL =
            "SELECT u.* FROM USUARIOS u JOIN ASIGNACIONES_TABLERO a ON u.ID = a.USUARIO_ID WHERE a.TABLERO_ID = ?";

    /**
     * Método helper para mapear Board desde ResultSet
     */
    private static Board mapearBoard(ResultSet rs) throws SQLException {
        Board tablero = new Board();
        tablero.setId(rs.getInt("ID"));
        tablero.setNombre(rs.getString("NOMBRE"));
        tablero.setPropietarioId(rs.getInt("PROPIETARIO_ID"));

        try {
            tablero.setDescripcion(rs.getString("DESCRIPCION"));
        } catch (SQLException e) {
            tablero.setDescripcion("");
        }

        return tablero;
    }

    /**
     * Método helper para mapear User desde ResultSet
     */
    private static User mapearUsuario(ResultSet rs) throws SQLException {
        User usuario = new User();
        usuario.setId(rs.getInt("ID"));
        usuario.setEmail(rs.getString("EMAIL"));
        usuario.setNombre(rs.getString("NOMBRE"));
        usuario.setRol(rs.getString("ROL"));
        return usuario;
    }

    /**
     * ObtenerTablerosAsignadosAUsuario
     */
    public static List<Board> obtenerTablerosAsignadosAUsuario(int usuarioId) {
        List<Board> tableros = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_TABLEROS_ASIGNADOS_SQL)) {

            pstmt.setInt(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tableros.add(mapearBoard(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tableros asignados: " + e.getMessage());
        }

        return tableros;
    }

    /**
     * método obtenerUsuariosAsignadosATablero
     */
    public static List<User> obtenerUsuariosAsignadosATablero(int tableroId) {
        List<User> usuarios = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_USUARIOS_ASIGNADOS_SQL)) {

            pstmt.setInt(1, tableroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios asignados: " + e.getMessage());
        }

        return usuarios;
    }


    public static boolean asignarTableroAMultiplesUsuarios(int tableroId, List<Integer> usuarioIds) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ASIGNAR_TABLERO_SQL)) {

            conn.setAutoCommit(false);

            for (Integer usuarioId : usuarioIds) {
                pstmt.setInt(1, tableroId);
                pstmt.setInt(2, usuarioId);
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            conn.commit();

            // Verificar que todas las operaciones fueron exitosas
            for (int result : results) {
                if (result <= 0) return false;
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error en asignación múltiple: " + e.getMessage());
            return false;
        }
    }


    public static boolean asignarTableroAUsuario(int tableroId, int usuarioId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ASIGNAR_TABLERO_SQL)) {

            pstmt.setInt(1, tableroId);
            pstmt.setInt(2, usuarioId);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al asignar tablero: " + e.getMessage());
            return false;
        }
    }

    public static boolean desasignarTableroDeUsuario(int tableroId, int usuarioId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DESASIGNAR_TABLERO_SQL)) {

            pstmt.setInt(1, tableroId);
            pstmt.setInt(2, usuarioId);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al desasignar tablero: " + e.getMessage());
            return false;
        }
    }
    /**
     * Elimina todas las asignaciones de tablero de un usuario específico
     */
    public static boolean eliminarTodasAsignacionesDeUsuario(int usuarioId) {
        String sql = "DELETE FROM ASIGNACIONES_TABLERO WHERE USUARIO_ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            int filasAfectadas = pstmt.executeUpdate();

            System.out.println("Asignaciones eliminadas para usuario " + usuarioId + ": " + filasAfectadas);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar asignaciones de usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un usuario tiene asignaciones de tablero
     */
    public static boolean usuarioTieneAsignaciones(int usuarioId) {
        String sql = "SELECT COUNT(*) as total FROM ASIGNACIONES_TABLERO WHERE USUARIO_ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar asignaciones: " + e.getMessage());
        }

        return false;
    }
}