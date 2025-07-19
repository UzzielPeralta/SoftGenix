package com.softgenix.Dao;

import com.softgenix.App.Config.Database;
import com.softgenix.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Verifica si ya existe un usuario con el email proporcionado
     */
    public static boolean existeEmail(String email) {
        String sql = "SELECT 1 FROM USUARIOS WHERE EMAIL = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Si hay resultados, el email existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Crea un nuevo usuario en la base de datos
     */
    public static boolean crearUsuario(User usuario) {
        String sql = "INSERT INTO USUARIOS (EMAIL, CONTRASENA, NOMBRE, ROL) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getEmail());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getNombre());
            pstmt.setString(4, usuario.getRol());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todos los usuarios del sistema
     */
    public static List<User> obtenerTodosUsuarios() {
        List<User> usuarios = new ArrayList<>();
        String sql = "SELECT ID, EMAIL, CONTRASENA, NOMBRE, ROL FROM USUARIOS ORDER BY ID";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User usuario = new User();
                usuario.setId(rs.getInt("ID"));
                usuario.setEmail(rs.getString("EMAIL"));
                usuario.setContrasena(rs.getString("CONTRASENA"));
                usuario.setNombre(rs.getString("NOMBRE"));
                usuario.setRol(rs.getString("ROL"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    /**
     * Obtiene usuarios filtrados por rol
     */
    public static List<User> obtenerUsuariosPorRol(String rol) {
        List<User> usuarios = new ArrayList<>();
        String sql = "SELECT ID, EMAIL, CONTRASENA, NOMBRE, ROL FROM USUARIOS WHERE ROL = ? ORDER BY NOMBRE";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rol);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User usuario = new User();
                    usuario.setId(rs.getInt("ID"));
                    usuario.setEmail(rs.getString("EMAIL"));
                    usuario.setContrasena(rs.getString("CONTRASENA"));
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

    /**
     * Elimina un usuario por su ID
     */
    public static boolean eliminarUsuario(int userId) {
        String sql = "DELETE FROM USUARIOS WHERE ID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca un usuario por su ID
     */
    public static User obtenerUsuarioPorId(int userId) {
        String sql = "SELECT ID, EMAIL, CONTRASENA, NOMBRE, ROL FROM USUARIOS WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User usuario = new User();
                    usuario.setId(rs.getInt("ID"));
                    usuario.setEmail(rs.getString("EMAIL"));
                    usuario.setContrasena(rs.getString("CONTRASENA"));
                    usuario.setNombre(rs.getString("NOMBRE"));
                    usuario.setRol(rs.getString("ROL"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene un usuario por sus credenciales para la autenticación
     */
    public static User obtenerUsuarioPorCredenciales(String email, String password) {
        String sql = "SELECT ID, EMAIL, CONTRASENA, NOMBRE, ROL FROM USUARIOS WHERE EMAIL = ? AND CONTRASENA = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User usuario = new User();
                    usuario.setId(rs.getInt("ID"));
                    usuario.setEmail(rs.getString("EMAIL"));
                    usuario.setContrasena(rs.getString("CONTRASENA"));
                    usuario.setNombre(rs.getString("NOMBRE"));
                    usuario.setRol(rs.getString("ROL"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}