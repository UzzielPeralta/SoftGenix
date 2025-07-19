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

    /**
     * Asigna un tablero a un usuario
     */
    public static boolean asignarTableroAUsuario(int tableroId, int usuarioId) {
        String sql = "INSERT INTO ASIGNACIONES_TABLERO (TABLERO_ID, USUARIO_ID) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableroId);
            pstmt.setInt(2, usuarioId);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina la asignación de un tablero a un usuario
     */
    public static boolean desasignarTableroDeUsuario(int tableroId, int usuarioId) {
        String sql = "DELETE FROM ASIGNACIONES_TABLERO WHERE TABLERO_ID = ? AND USUARIO_ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableroId);
            pstmt.setInt(2, usuarioId);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene los tableros asignados a un usuario específico
     */
    public static List<Board> obtenerTablerosAsignadosAUsuario(int usuarioId) {
        List<Board> tableros = new ArrayList<>();
        String sql = "SELECT t.* FROM TABLEROS t " +
                "JOIN ASIGNACIONES_TABLERO a ON t.ID = a.TABLERO_ID " +
                "WHERE a.USUARIO_ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Board tablero = new Board();
                    tablero.setId(rs.getInt("ID"));
                    tablero.setNombre(rs.getString("NOMBRE"));
                    tablero.setPropietarioId(rs.getInt("CREADOR_ID"));
                    tableros.add(tablero);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tableros;
    }

    /**
     * Obtiene los usuarios asignados a un tablero específico
     */
    public static List<User> obtenerUsuariosAsignadosATablero(int tableroId) {
        List<User> usuarios = new ArrayList<>();
        String sql = "SELECT u.* FROM USUARIOS u " +
                "JOIN ASIGNACIONES_TABLERO a ON u.ID = a.USUARIO_ID " +
                "WHERE a.TABLERO_ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User usuario = new User();
                    usuario.setId(rs.getInt("ID"));
                    usuario.setEmail(rs.getString("EMAIL"));
                    usuario.setNombre(rs.getString("NOMBRE"));
                    usuario.setRol(rs.getString("ROL"));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }
}
