package com.softgenix.Application;

import java.sql.*;
import java.util.Properties;

public class Database {

    // --- La configuración de la conexión sigue igual y es correcta ---
    private static final String DB_ALIAS = "ericklara";
    private static final String USERNAME = "ADMIN";
    private static final String PASSWORD = "Zendo1234***";
    private static final String WALLET_PATH = "C:\\Users\\erick\\Downloads\\Wallet_Zendo";

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:oracle:thin:@" + DB_ALIAS;
        Properties props = new Properties();
        props.setProperty("user", USERNAME);
        props.setProperty("password", PASSWORD);
        props.setProperty("oracle.net.tns_admin", WALLET_PATH);
        props.setProperty("oracle.net.ssl_server_dn_match", "true");
        return DriverManager.getConnection(url, props);
    }



    /**
     * Añade un nuevo usuario a la base de datos.
     */
    public static boolean addUser(String nombreUsuario, String email, String password, String rol) {
        // SQL actualizado para coincidir con la tabla USUARIOS
        String sql = "INSERT INTO USUARIOS(NOMBRE_USUARIO, EMAIL, CONTRASENA, ROL) VALUES(?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // Guardar la contraseña en texto plano
            pstmt.setString(4, rol);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida un usuario comparando el email y la contraseña en texto plano.
     */
    public static boolean validateUser(String email, String password) {
        // SQL actualizado para buscar por email y contraseña
        String sql = "SELECT 1 FROM USUARIOS WHERE EMAIL = ? AND CONTRASENA = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Si rs.next() es true, significa que se encontró una fila, por lo tanto el usuario es válido.
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error al validar el usuario: " + e.getMessage());
            return false;
        }
    }
}