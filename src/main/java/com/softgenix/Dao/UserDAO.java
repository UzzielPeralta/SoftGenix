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

    // Constantes SQL
    private static final String EXISTE_EMAIL_SQL = "SELECT 1 FROM USUARIOS WHERE EMAIL = ?";
    private static final String CREAR_USUARIO_SQL = "INSERT INTO USUARIOS (EMAIL, CONTRASENA, NOMBRE, ROL) VALUES (?, ?, ?, ?)";
    private static final String OBTENER_TODOS_SQL = "SELECT ID, EMAIL, CONTRASENA, NOMBRE, ROL FROM USUARIOS ORDER BY ID";
    private static final String OBTENER_POR_ROL_SQL = "SELECT ID, EMAIL, CONTRASENA, NOMBRE, ROL FROM USUARIOS WHERE ROL = ? ORDER BY NOMBRE";
    private static final String OBTENER_POR_ID_SQL = "SELECT ID, EMAIL, CONTRASENA, NOMBRE, ROL FROM USUARIOS WHERE ID = ?";
    private static final String OBTENER_POR_CREDENCIALES_SQL = "SELECT ID, EMAIL, CONTRASENA, NOMBRE, ROL FROM USUARIOS WHERE EMAIL = ? AND CONTRASENA = ?";
    private static final String ELIMINAR_USUARIO_SQL = "DELETE FROM USUARIOS WHERE ID = ?";


    private static User mapearUsuario(ResultSet rs) throws SQLException {
        User usuario = new User();
        usuario.setId(rs.getInt("ID"));
        usuario.setEmail(rs.getString("EMAIL"));
        usuario.setContrasena(rs.getString("CONTRASENA"));
        usuario.setNombre(rs.getString("NOMBRE"));
        usuario.setRol(rs.getString("ROL"));
        return usuario;
    }

    /**
     * Verifica si ya existe un usuario con el email
     */
    public static boolean existeEmail(String email) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(EXISTE_EMAIL_SQL)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Crea un nuevo usuario en la base de datos
     */
    public static boolean crearUsuario(User usuario) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(CREAR_USUARIO_SQL)) {

            pstmt.setString(1, usuario.getEmail());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getNombre());
            pstmt.setString(4, usuario.getRol());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los usuarios del sistema
     */
    public static List<User> obtenerTodosUsuarios() {
        List<User> usuarios = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_TODOS_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    /**
     * Obtiene usuarios filtrados por rol
     */
    public static List<User> obtenerUsuariosPorRol(String rol) {
        List<User> usuarios = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_POR_ROL_SQL)) {

            pstmt.setString(1, rol);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios por rol: " + e.getMessage());
        }

        return usuarios;
    }

    /**
     * Busca un usuario por su ID
     */
    public static User obtenerUsuarioPorId(int userId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_POR_ID_SQL)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Obtiene un usuario por sus credenciales para la autenticación
     */
    public static User obtenerUsuarioPorCredenciales(String email, String password) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_POR_CREDENCIALES_SQL)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por credenciales: " + e.getMessage());
        }

        return null;
    }

    /**
     * Elimina un usuario por su ID
     */
    public static boolean eliminarUsuario(int userId) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Eliminar asignaciones de tarjetas
                String eliminarAsignacionesTarjetasSQL = "DELETE FROM ASIGNACIONES_TARJETA WHERE USUARIO_ID = ?";
                try (PreparedStatement pstmt1 = conn.prepareStatement(eliminarAsignacionesTarjetasSQL)) {
                    pstmt1.setInt(1, userId);
                    pstmt1.executeUpdate();
                }

                // 2. Eliminar asignaciones de tableros
                String eliminarAsignacionesTablerosSQL = "DELETE FROM ASIGNACIONES_TABLERO WHERE USUARIO_ID = ?";
                try (PreparedStatement pstmt2 = conn.prepareStatement(eliminarAsignacionesTablerosSQL)) {
                    pstmt2.setInt(1, userId);
                    pstmt2.executeUpdate();
                }

                // 3. Finalmente eliminar el usuario
                try (PreparedStatement pstmt3 = conn.prepareStatement(ELIMINAR_USUARIO_SQL)) {
                    pstmt3.setInt(1, userId);
                    int filasAfectadas = pstmt3.executeUpdate();

                    if (filasAfectadas > 0) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
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

    /**
     * Cuenta la cantidad total de usuarios por rol
     */
    public static int contarUsuariosPorRol(String rol) {
        String sql = "SELECT COUNT(*) as total FROM USUARIOS WHERE ROL = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rol);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al contar usuarios: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Actualiza la información básica de un usuario
     */
    public static boolean actualizarUsuario(User usuario) {
        String sql = "UPDATE USUARIOS SET EMAIL = ?, NOMBRE = ?, ROL = ? WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getEmail());
            pstmt.setString(2, usuario.getNombre());
            pstmt.setString(3, usuario.getRol());
            pstmt.setInt(4, usuario.getId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza solo la contraseña de un usuario
     */
    public static boolean actualizarContrasena(int userId, String nuevaContrasena) {
        String sql = "UPDATE USUARIOS SET CONTRASENA = ? WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevaContrasena);
            pstmt.setInt(2, userId);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }
}